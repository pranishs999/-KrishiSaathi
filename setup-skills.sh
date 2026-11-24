#!/usr/bin/env bash
# Antigravity 14-skill installer and manager
# Target: Fedora/Linux, project-local Antigravity skills.
# Usage: ./setup-skills.sh [install|status|update|remove|help]
set -Eeuo pipefail

ROOT="$(pwd)"
SKILLS_DIR="$ROOT/.agents/skills"
STATE_DIR="$ROOT/.antigravity-skill-manager"
LOG_FILE="$STATE_DIR/install.log"
TOTAL=14

SKILLS=(
  android-dev
  ui-skills
  baseline-ui
  fixing-accessibility
  postgres-best-practices
  database-security
  api-security
  find-bugs
  lint-and-validate
  fix-review
  screenshots
  context-fundamentals
  context-optimization
  readme
)

# Upstream repository mapping. Override any entry with:
# SKILL_SOURCE_<skill-name>=owner/repository
declare -A SOURCES=(
  [android-dev]="sickn33/agentic-awesome-skills"
  [ui-skills]="ibelick/ui-skills"
  [baseline-ui]="ibelick/ui-skills"
  [fixing-accessibility]="ibelick/ui-skills"
  [postgres-best-practices]="sickn33/agentic-awesome-skills"
  [database-security]="trailofbits/skills"
  [api-security]="skillmds/skillmd"
  [find-bugs]="getsentry/skills"
  [lint-and-validate]="vudovn/antigravity-kit"
  [fix-review]="trailofbits/skills"
  [screenshots]="sickn33/agentic-awesome-skills"
  [context-fundamentals]="muratcankoylan/Agent-Skills-for-Context-Engineering"
  [context-optimization]="muratcankoylan/Agent-Skills-for-Context-Engineering"
  [readme]="Shpigford/skills"
)

# Skill directory names differ from requested labels in some upstream repositories.
declare -A UPSTREAM_NAMES=(
  [postgres-best-practices]="postgres-best-practices"
)

mkdir -p "$STATE_DIR"
touch "$LOG_FILE"

log()  { printf '[%s] %s\n' "$(date '+%F %T')" "$*" | tee -a "$LOG_FILE"; }
good() { printf '\033[32m[OK]\033[0m %s\n' "$*"; }
warn() { printf '\033[33m[WARN]\033[0m %s\n' "$*"; }
bad()  { printf '\033[31m[FAIL]\033[0m %s\n' "$*" >&2; }

usage() {
  cat <<'EOF'
Antigravity 14-skill manager

Commands:
  install       Install all 14 skills (default)
  status        Show installed/missing status
  update        Refresh installed skills
  remove        Remove these 14 project-local skills
  help          Show this help

Examples:
  chmod +x setup-skills.sh
  ./setup-skills.sh
  ./setup-skills.sh status
  ./setup-skills.sh update
  ./setup-skills.sh remove

Run from the root of the project where .agents/skills should be created.
Optional source override:
  SKILL_SOURCE_screenshots=owner/repo ./setup-skills.sh install
EOF
}

skill_path() { printf '%s/%s/SKILL.md' "$SKILLS_DIR" "$1"; }

status() {
  local installed=0 missing=0
  printf '\nAntigravity skills: %s\n\n' "$SKILLS_DIR"
  printf '%-30s %s\n' "SKILL" "STATUS"
  printf '%-30s %s\n' "------------------------------" "--------"
  for skill in "${SKILLS[@]}"; do
    if [[ -s "$(skill_path "$skill")" ]]; then
      printf '%-30s \033[32m%s\033[0m\n' "$skill" "INSTALLED"
      ((installed+=1))
    else
      printf '%-30s \033[31m%s\033[0m\n' "$skill" "MISSING"
      ((missing+=1))
    fi
  done
  printf '\nProgress: %s/%s installed; %s missing.\n\n' "$installed" "$TOTAL" "$missing"
  [[ "$missing" -eq 0 ]]
}

check_prerequisites() {
  command -v node >/dev/null 2>&1 || {
    bad "Node.js is required. Install Node.js 20+ and retry."
    return 1
  }
  command -v npm >/dev/null 2>&1 || {
    bad "npm is required but was not found."
    return 1
  }
  command -v git >/dev/null 2>&1 || {
    bad "Git is required but was not found."
    return 1
  }
  command -v curl >/dev/null 2>&1 || {
    bad "curl is required but was not found."
    return 1
  }
  local major
  major="$(node -p 'Number(process.versions.node.split(".")[0])')"
  if (( major < 20 )); then
    bad "Node.js 20+ required; found $(node --version)."
    return 1
  fi
  good "Prerequisites available: Node $(node --version), npm $(npm --version), Git."
}

install_one() {
  local skill="$1"
  local repo="${SOURCES[$skill]}"
  local upstream="${UPSTREAM_NAMES[$skill]:-$skill}"
  local env_key="SKILL_SOURCE_${skill//-/_}"
  local override="${!env_key:-}"
  [[ -n "$override" ]] && repo="$override"

  log "Installing $skill from $repo (upstream skill: $upstream)"
  mkdir -p "$SKILLS_DIR"

  # The CLI installs into the project-local .agents/skills directory.
  # A failure is recorded per skill; it does not stop the remaining installs.
  if npx --yes skills add "$repo" \
      --skill "$upstream" \
      -a antigravity \
      -y 2>&1 | tee -a "$LOG_FILE"; then
    if [[ -s "$(skill_path "$skill")" ]]; then
      good "$skill installed."
      return 0
    fi

    # Some upstream skills use a different folder name. Do not silently count
    # a missing requested folder as a successful installation.
    if [[ -s "$(skill_path "$upstream")" ]]; then
      if [[ "$upstream" != "$skill" ]]; then
        mv "$SKILLS_DIR/$upstream" "$SKILLS_DIR/$skill"
      fi
      good "$skill installed."
      return 0
    fi
    warn "$skill command completed, but expected SKILL.md was not found."
    return 1
  else
    bad "Installation failed for $skill. See $LOG_FILE"
    return 1
  fi
}

install_all() {
  check_prerequisites || exit 1
  local success=0 failed=0
  mkdir -p "$SKILLS_DIR"

  printf '\nInstalling %s Antigravity skills into:\n%s\n\n' "$TOTAL" "$SKILLS_DIR"
  for skill in "${SKILLS[@]}"; do
    if install_one "$skill"; then
      ((success+=1))
    else
      ((failed+=1))
    fi
    printf '\n'
  done

  printf '\n========== INSTALLATION SUMMARY ==========\n'
  printf 'Installed in this run: %s\nFailed/not verified:   %s\n' "$success" "$failed"
  printf 'Log: %s\n' "$LOG_FILE"
  printf '==========================================\n\n'

  status || true
  (( failed == 0 ))
}

update_all() {
  check_prerequisites || exit 1
  log "Updating the 14 requested skills."
  # Re-run the official Skills CLI installer. Existing skill files may be
  # replaced by the CLI; back up the current skill tree first.
  local backup="$STATE_DIR/skills-backup-$(date +%Y%m%d-%H%M%S).tar.gz"
  if [[ -d "$SKILLS_DIR" ]]; then
    tar -czf "$backup" -C "$ROOT" .agents/skills
    good "Backup created: $backup"
  fi
  # Remove only the managed skill directories after the backup so the CLI
  # fetches fresh upstream copies instead of leaving stale files untouched.
  for skill in "${SKILLS[@]}"; do
    upstream="${UPSTREAM_NAMES[$skill]:-$skill}"
    rm -rf -- "$SKILLS_DIR/$skill"
    [[ "$upstream" == "$skill" ]] || rm -rf -- "$SKILLS_DIR/$upstream"
  done
  install_all
}

remove_all() {
  printf 'This removes only the 14 listed skill folders from:\n%s\n' "$SKILLS_DIR"
  read -r -p "Type REMOVE to continue: " answer
  [[ "$answer" == "REMOVE" ]] || { warn "Cancelled."; return 0; }

  for skill in "${SKILLS[@]}"; do
    if [[ -d "$SKILLS_DIR/$skill" ]]; then
      rm -rf -- "$SKILLS_DIR/$skill"
      good "Removed $skill"
    else
      warn "$skill is not installed."
    fi
  done
  status || true
}

command="${1:-install}"
case "$command" in
  install) install_all ;;
  status) status ;;
  update) update_all ;;
  remove) remove_all ;;
  help|-h|--help) usage ;;
  *)
    bad "Unknown command: $command"
    usage
    exit 2
    ;;
esac
