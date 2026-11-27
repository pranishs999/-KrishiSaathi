import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const corsHeaders = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers":
    "authorization, x-client-info, apikey, content-type",
};

interface RegistrationPayload {
  full_name: string;
  phone: string;
  municipality: string;
  ward_number: number;
  tole_village?: string;
  consent_status: boolean;
  registered_at: number;
}

function validatePhone(phone: string): boolean {
  const cleaned = phone.replace(/[^0-9]/g, "");
  return /^(97|98)\d{8}$/.test(cleaned);
}

function validatePayload(body: RegistrationPayload): string | null {
  if (!body.full_name || body.full_name.trim().length === 0) {
    return "कृपया किसानको नाम लेख्नुहोस्।";
  }
  if (!body.phone || !validatePhone(body.phone)) {
    return "कृपया ९८ वा ९७ बाट सुरु हुने १० अंकको मान्य नेपाली मोबाइल नम्बर लेख्नुहोस्।";
  }
  if (
    !body.ward_number ||
    body.ward_number < 1 ||
    body.ward_number > 12
  ) {
    return "कृपया वडा नम्बर छान्नुहोस्।";
  }
  if (!body.municipality || body.municipality.trim().length === 0) {
    return "गाउँपालिकाको नाम आवश्यक छ।";
  }
  if (body.consent_status !== true) {
    return "कृपया सहमति दिनुहोस्।";
  }
  return null;
}

Deno.serve(async (req: Request) => {
  // Handle CORS preflight
  if (req.method === "OPTIONS") {
    return new Response("ok", { headers: corsHeaders });
  }

  try {
    const body: RegistrationPayload = await req.json();

    // Server-side validation
    const validationError = validatePayload(body);
    if (validationError) {
      return new Response(
        JSON.stringify({ success: false, error: validationError }),
        {
          status: 400,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    // Normalize phone
    const normalizedPhone = body.phone.replace(/[^0-9]/g, "");

    // Create Supabase admin client with service role key (server-side only)
    const supabaseUrl = Deno.env.get("SUPABASE_URL")!;
    const serviceRoleKey = Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!;
    const supabase = createClient(supabaseUrl, serviceRoleKey);

    // Check for duplicate phone
    const { data: existing } = await supabase
      .from("farmer_profiles")
      .select("id, phone")
      .eq("phone", normalizedPhone)
      .maybeSingle();

    if (existing) {
      // Update existing record instead of failing
      const { error: updateError } = await supabase
        .from("farmer_profiles")
        .update({
          full_name: body.full_name.trim(),
          municipality: body.municipality.trim(),
          ward_number: body.ward_number,
          tole_village: (body.tole_village || "").trim(),
          consent_status: body.consent_status,
          registered_at: body.registered_at || Date.now(),
          updated_at: new Date().toISOString(),
        })
        .eq("phone", normalizedPhone);

          if (updateError) {
        return new Response(
          JSON.stringify({
            success: false,
            error: "डाटाबेसमा अपडेट गर्न असफल।",
          }),
          {
            status: 500,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
          }
        );
      }

      return new Response(
        JSON.stringify({
          success: true,
          is_update: true,
          message: "किसान विवरण अपडेट भयो।",
        }),
        {
          status: 200,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    // Insert new farmer record
    const { data: inserted, error: insertError } = await supabase
      .from("farmer_profiles")
      .insert({
        phone: normalizedPhone,
        full_name: body.full_name.trim(),
        municipality: body.municipality.trim(),
        ward_number: body.ward_number,
        tole_village: (body.tole_village || "").trim(),
        consent_status: body.consent_status,
        registered_at: body.registered_at || Date.now(),
        mpin_hash: "bakaiya_app_no_auth",
      })
      .select("id")
      .single();

    if (insertError) {
      // Handle unique constraint violation (race condition)
      if (insertError.code === "23505") {
        return new Response(
          JSON.stringify({
            success: true,
            is_update: false,
            message: "किसान पहिले नै दर्ता भइसकेको छ।",
          }),
          {
            status: 200,
            headers: { ...corsHeaders, "Content-Type": "application/json" },
          }
        );
      }

      return new Response(
        JSON.stringify({
          success: false,
          error: "डाटाबेसमा दर्ता गर्न असफल।",
        }),
        {
          status: 500,
          headers: { ...corsHeaders, "Content-Type": "application/json" },
        }
      );
    }

    return new Response(
      JSON.stringify({
        success: true,
        is_update: false,
        farmer_id: inserted?.id,
        message: "किसान दर्ता सफल भयो।",
      }),
      {
        status: 201,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  } catch (err) {
    return new Response(
        JSON.stringify({
        success: false,
        error: "अनपेक्षित त्रुटि भयो।",
      }),
      {
        status: 500,
        headers: { ...corsHeaders, "Content-Type": "application/json" },
      }
    );
  }
});
