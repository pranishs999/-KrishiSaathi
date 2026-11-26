# Content Verification & Provenance Workflow

## 1. Mandatory Review Lifecycle

```text
  +-------+      missing citation      +--------------+
  | Draft | -------------------------> | Needs Source |
  +---+---+                            +------+-------+
      |                                       |
      | submit for review                     | citation attached
      v                                       v
  +-----------+   reject + comments    +--------------+
  | In Review | ---------------------> |    Draft     |
  +-----+-----+                        +--------------+
        |
        | Technical Reviewer / Super Admin approval
        v
  +----------+    publish gate checks pass    +-----------+
  | Approved | -----------------------------> | Published |
  +----------+                                +-----+-----+
                                                    |
                                 scheduled review   | due / outdated
                                                    v
                                            +-----------------+
                                            | Needs Re-review |
                                            +-----------------+
```

## 2. Enforcement Mechanisms (Backend + Database)
1. **No Self-Publishing by Content Editors:** `Content Editor` accounts may only transition items between `draft`, `needs_source`, and `in_review`. Transitions to `approved` or `published` require `Technical Reviewer` / `Super Admin` privileges enforced both in server API views (`admin_web`) and PostgreSQL `BEFORE UPDATE` triggers.
2. **Pre-Publication Validation Gate:**
   - Every published crop or animal record must pass required-field checks, media license approval (`permission_status = 'approved'`), and at least one verified `content_citations` link to a `source_documents` record.
   - Records or individual sub-sections lacking primary NARC / MoALD / DLS / FAO verification remain `needs_source` or `in_review` and **never** enter the public farmer feed.
3. **Separate Domain Approval Scopes:**
   - `crop_technical` (NARC / MoALD agronomy)
   - `animal_husbandry` (DLS / NARC / FANSEP housing, feeding, breeding, production)
   - `veterinary_health` (qualified veterinary/animal health safety verification)
   - `media_license` (Creative Commons / municipal permission verification)
   - `video_permission` (YouTube terms vs explicit MP4 download rights)
   - `helpline_verification` (official municipal / district office number verification)
4. **Veterinary Safety Policy:**
   - Educational health & vaccination frameworks reference official Department of Livestock Services (DLS) and National Vaccine Production Laboratory (NVPL) guidelines without inventing prescription dosages or diagnosis protocols.
   - Every livestock view surfaces an unmistakable advisory telling farmers to contact a qualified veterinarian or local livestock service centre upon clinical warning signs.
