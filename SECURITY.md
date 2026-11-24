# Krishi Saathi: Security Architecture & Verification Protocols

---

## 1. Security Philosophy

Krishi Saathi operates in a high-consequence agricultural domain where security extends beyond digital data protection to **physical and biological safety**:
- **Data Protection:** Standard cryptographic protection of farmer identity and session tokens.
- **Physical Safety:** Defense against poisoned, inaccurate, or malicious agro-chemical advisories that could destroy crops, poison soil, or endanger farmer health.

---

## 2. Supabase Row Level Security (RLS) Policies

All tables in the Supabase PostgreSQL database have Row Level Security **strictly enabled**. By default, all public client operations are blocked unless an explicit permissive policy allows it.

### 2.1 Profiles Table Policies
Farmers can only access and modify their own personal profile data.

```sql
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Policy 1: Authenticated users can view only their own profile
CREATE POLICY "Users can view own profile" 
ON public.profiles
FOR SELECT 
TO authenticated 
USING (auth.uid() = id);

-- Policy 2: Users can insert their own profile upon registration
CREATE POLICY "Users can insert own profile" 
ON public.profiles
FOR INSERT 
TO authenticated 
WITH CHECK (auth.uid() = id);

-- Policy 3: Users can update their own profile
CREATE POLICY "Users can update own profile" 
ON public.profiles
FOR UPDATE 
TO authenticated 
USING (auth.uid() = id)
WITH CHECK (auth.uid() = id);

-- Policy 4: Admins can view all profiles for support and verification
CREATE POLICY "Admins can view all profiles"
ON public.profiles
FOR SELECT
TO authenticated
USING (
    EXISTS (
        SELECT 1 FROM public.profiles 
        WHERE id = auth.uid() AND role = 'admin'
    )
);
```

### 2.2 Agricultural Advisory Tables Policies
Tables: `crops`, `crop_guides`, `seasonal_recommendations`, `pest_diseases`, `fertilizer_guides`, `pesticide_guides`.

**Rule:** Public/authenticated users get **READ-ONLY** access to verified records. Only verified `admin` accounts can create, update, or delete agronomic guidance.

```sql
-- Enable RLS on all advisory tables
ALTER TABLE public.crops ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.crop_guides ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.seasonal_recommendations ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.pest_diseases ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.fertilizer_guides ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.pesticide_guides ENABLE ROW LEVEL SECURITY;

-- 1. Crops: Public read for active crops
CREATE POLICY "Anyone can read active crops"
ON public.crops FOR SELECT
TO anon, authenticated
USING (is_active = TRUE);

CREATE POLICY "Admins have full access to crops"
ON public.crops FOR ALL
TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role = 'admin'));

-- 2. Crop Guides: Public read
CREATE POLICY "Anyone can read crop guides"
ON public.crop_guides FOR SELECT
TO anon, authenticated
USING (TRUE);

CREATE POLICY "Admins have full access to crop guides"
ON public.crop_guides FOR ALL
TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role = 'admin'));

-- 3. Seasonal Recommendations: Public read
CREATE POLICY "Anyone can read seasonal recommendations"
ON public.seasonal_recommendations FOR SELECT
TO anon, authenticated
USING (TRUE);

CREATE POLICY "Admins have full access to seasonal recommendations"
ON public.seasonal_recommendations FOR ALL
TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role = 'admin'));

-- 4. Pest & Diseases: Public read
CREATE POLICY "Anyone can read pest and diseases"
ON public.pest_diseases FOR SELECT
TO anon, authenticated
USING (TRUE);

CREATE POLICY "Admins have full access to pest diseases"
ON public.pest_diseases FOR ALL
TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role = 'admin'));

-- 5. Fertilizer Guides: Public read
CREATE POLICY "Anyone can read fertilizer guides"
ON public.fertilizer_guides FOR SELECT
TO anon, authenticated
USING (TRUE);

CREATE POLICY "Admins have full access to fertilizer guides"
ON public.fertilizer_guides FOR ALL
TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role = 'admin'));

-- 6. Pesticide Guides: Strict expert verification check
CREATE POLICY "Anyone can read verified pesticide guides"
ON public.pesticide_guides FOR SELECT
TO anon, authenticated
USING (verified_by_expert = TRUE);

CREATE POLICY "Admins have full access to pesticide guides"
ON public.pesticide_guides FOR ALL
TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE id = auth.uid() AND role = 'admin'));
```

---

## 3. Android Keystore & Sensitive Token Storage

1. **Hardware-Backed Cryptography:**
   - On devices running API 26+, tokens are secured via the Android Keystore using **AES-256-GCM** encryption keys generated in hardware (TEE or StrongBox).
2. **Jetpack Security Integration:**
   - Tokens (Supabase JWT, Refresh Token) are stored using `androidx.security.crypto.EncryptedSharedPreferences`:
   ```kotlin
   val masterKey = MasterKey.Builder(context)
       .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
       .build()

   val securePrefs = EncryptedSharedPreferences.create(
       context,
       "krishi_saathi_secure_prefs",
       masterKey,
       EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
       EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
   )
   ```
3. **No Committed Secrets:**
   - `local.properties` holds sensitive environment variables (`SUPABASE_URL`, `SUPABASE_ANON_KEY`).
   - The Supabase Anon key is designed to be public-safe *strictly because RLS is fully enforced on every table*.
   - Never commit `service_role` keys into the Android application under any circumstances.

---

## 4. Phone OTP Authentication Protocols

- **Phone Number Format:** Strict E.164 standard for Nepal numbers (`+977-98XXXXXXXX` or `+977-97XXXXXXXX`).
- **OTP Verification Safeguards:**
  - 6-digit numeric OTP with 60-second cool-down timer on the client before resending.
  - Rate limiting enforced on Supabase Auth to prevent SMS abuse and brute force attempts.
- **Offline Guest Fallback:**
  - Farmers with zero cellular signal during app setup can bypass OTP onboarding to create a local-only guest session in Room, allowing instant access to offline crop data.
  - The app prompts the user to verify phone number once connectivity returns to enable profile cloud backup.

---

## 5. Strict Pesticide Advisory Verification Rules

Pesticide misinformation poses acute health and environmental risks. Krishi Saathi enforces strict verification standards before displaying any chemical recommendation:

### 5.1 Verification Checklist
Every chemical entry in `pesticide_guides` must satisfy five mandatory criteria:
1. **MoALD / PQPMC Registration:** Must possess an active registration code issued by the Plant Quarantine and Pesticide Management Centre (Nepal).
2. **Expert Verification Flag:** Must have `verified_by_expert = true` in PostgreSQL. Unverified entries are quarantined.
3. **Exact Pre-Harvest Interval (PHI):** Must specify explicit waiting days before harvest. Entries with null or "unknown" PHI are rejected by schema constraints.
4. **WHO Toxicity Classification Badge:** Must specify toxicity color:
   - 🟥 Red (Class Ia/Ib)
   - 🟨 Yellow (Class II)
   - 🟦 Blue (Class III)
   - 🟩 Green (Class IV)
5. **Mandatory Non-Chemical Precursor:** The UI must visually place cultural and biological controls (neem oil, light traps, Trichoderma) **above** chemical recommendations on the screen.

### 5.2 Banned Substance Blacklist
The app enforces an explicit ban on banned organochlorine and high-hazard pesticides prohibited under Nepal Gazette notifications (including Endosulfan, Aldrin, Dieldrin, Chlordane, Monocrotophos, Phorate, and Methyl Parathion). Any attempt to insert these chemicals will be blocked.

