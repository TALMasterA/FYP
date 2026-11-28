# Demo spending and rollout

## Branch controls

App Check and Firebase Auth apply to all five provider callables. Each validated request reserves its maximum provider usage in a Firestore transaction shared by all accounts. The application prices dispatch in integer nanodollars, where US$1 equals 1,000,000,000 nanodollars. A separate limit caps each unit. Missing, invalid or exhausted configuration pauses the affected operation. Other app features can continue.

| Meter | Reservation |
|---|---|
| Translator characters | Single translation and detection use the request text length in UTF-16 code units, a conservative count for Unicode characters. A batch reserves four times the total for its initial attempts and up to three retries per chunk. Batches accept up to 800 items, 5,000 code units each and 50,000 total. |
| Learning input and output | Input reserves one token per UTF-8 byte for the prompt and system message plus 256 tokens of framing. The output reserves the full `max_completion_tokens` value of 4,096. Enable only the named deployment, after verifying that its tokenizer and gateway overhead fit this bound and that it enforces this output setting including reasoning tokens. |
| Speech token grants | One grant is reserved at a zero rate. Enable grants only with an independently verified F0 cutoff for the resource bound to the Speech secret. Azure's STS token lasts ten minutes and can serve multiple SDK calls. Firebase cannot infer audio hours or synthesis characters from issuance. [Azure Speech authentication](https://learn.microsoft.com/en-us/azure/ai-services/speech-service/rest-text-to-speech#authentication) |

Two instances per provider function can accept up to four simultaneous requests each, or two each for generation. Scaled instance counts limit parallel traffic. Requests can continue arriving for the rest of the month.

Accounting uses the UTC month at request admission. Transactions synchronize policy reads, shared counters and optional request IDs. The app generates an ID per operation and sends the same ID in transport retries. A repeat sends zero provider requests after the first reservation. Older clients omit the ID and each dispatch reserves its full cost. The server keeps the reservation when it cannot determine the provider outcome and saves neither prompts nor provider responses in the quota ledger. Start a new operation after an error if a result is needed. For controls that depend on the provider's billing month or use delayed metering, leave additional month-end headroom.

## Policy format

Provision `provider_allowances/YYYY-MM` with the Firebase administrator identity. Omitted meters are paused. All counters and rates are whole numbers. Do not edit `provider_usage/{month}` or `provider_requests/{hash}` as part of rollover. Existing Firestore rules deny these collections to Android clients.

The example below is for a dedicated F0 Translator and F0 Speech pair with verified provider cutoffs. The 200,000-character allowance is an intentionally small demo policy. Learning stays paused until its gateway is verified.

```json
{
  "budgetNanodollars": 0,
  "translatorCharacters": {"maxUnits": 200000, "nanodollarsPerUnit": 0},
  "speechF0Verified": true,
  "speechTokens": {"maxUnits": 100, "nanodollarsPerUnit": 0}
}
```

For learning, configure `learningDeployment`, `learningInputTokens` and `learningOutputTokens` with the verified model and rates. Convert an invoice price per million units using `ceil(USD per million × 1000)`. For example, US$10 per million Translator characters is 10,000 nanodollars per character. Use the full marginal rate on any paid tier. A zero rate needs independent evidence that those units cannot create a charge. The application rejects budgets above US$1.999999999. Use a lower budget after accounting for fixed costs and headroom.

## Reproduce the combined estimate

Use project-filtered Google Cloud Billing reports or an existing billing export and Azure Cost Management Cost Analysis for the relevant resources. For each month record gross cost, credits, net cost, billing currency, resource or SKU, region and metered use. Use current prices for post-credit projections. Calculate:

```text
provider cash allowance = max(0, 2.00 - projected non-provider costs - headroom)
provider reservation USD = sum(maximum units × USD per unit)
```

- **Compute and requests.** Gen 2 Functions use Cloud Run. Include invocation CPU, memory, request time, networking and the usage of background functions. Account-level free allowances may be shared. [Cloud Run pricing](https://cloud.google.com/run/pricing)
- **Firestore and logs.** Include reads, writes, deletes, TTL, storage, indexes, transfer and log ingestion/retention. Firestore and logging have free quotas. Firestore TTL deletes require billing. Denied or exhausted application calls can still incur Google Cloud work. [Firestore free quotas](https://docs.cloud.google.com/firestore/quotas) · [Cloud Logging pricing](https://cloud.google.com/products/observability/pricing)
- **Scheduled work and build storage.** The first three Cloud Scheduler jobs per billing account are free. Additional jobs cost approximately US$0.10 per job-month. Include Cloud Build minutes, Artifact Registry images, source buckets, egress and email delivery. Artifact Registry currently provides 0.5 GiB-month free per account, then approximately US$0.10 per GiB-month. Estimate steady storage from monitored GiB-hours. [Cloud Scheduler pricing](https://cloud.google.com/scheduler/pricing) · [Artifact Registry pricing](https://cloud.google.com/artifact-registry/pricing) · [Cloud Build pricing](https://cloud.google.com/build/pricing)
- **Azure.** Translator F0 includes up to 2 million characters per month. Speech F0 includes 5 hours of standard real-time transcription and 0.5 million neural synthesis characters per month. Verify the tier of the bound resources and their renewal state. Check GenAI pricing and cutoff with the gateway operator. [Translator pricing](https://azure.microsoft.com/en-us/pricing/details/translator/) · [Speech pricing](https://azure.microsoft.com/en-us/pricing/details/speech/)
- **Alerts and caps.** Consider project-wide actual alerts at US$1.00, US$1.50 and US$1.80 with the owner and a verified backup recipient. Conventional budgets alert. Google Cloud offers service-level enforcement for eligible project/service pairs, including Cloud Run and Cloud Run functions. A cap can pause the affected workload and leaves services outside its scope accruing charges. Azure's subscription spending limit depends on the offer and follows its credit balance. [Google budgets](https://docs.cloud.google.com/billing/docs/how-to/budgets) · [Eligible Google spend caps](https://docs.cloud.google.com/billing/docs/how-to/budgets-spend-caps) · [Azure spending limits](https://learn.microsoft.com/en-us/azure/cost-management-billing/manage/spending-limit)

## Owner-approved production rollout

1. Confirm project, owner, billing notices, current and previous project costs, Azure tiers and secret bindings. Use a dedicated F0 Speech resource. Keep learning paused if output caps or rates are unverified. Stage current-month and next-month policies, with only confirmed meters, via the console under Firestore. Review open and retired secret versions separately.
2. After approval, deploy the reviewed revision. With the existing project explicitly selected, run `firebase deploy --project <project-id> --only functions:translateText,functions:translateTexts,functions:detectLanguage,functions:getSpeechToken,functions:generateLearningContent,functions:sendChatNotification,functions:sendFriendRequestNotification,functions:sendRequestAcceptedNotification,functions:sendSharedInboxNotification,functions:pruneStaleTokens,functions:deleteAccountAndData` from `fyp-backend`. The rules already have a default deny for the new paths. A separate rules deployment is required only when verified live rules differ.
3. Review TTL for `provider_requests.expiresAt` and existing `mail.ttlAt`. Firestore schedules these deletes asynchronously and bills TTL operations. Enable the chosen policies separately with approval. Leave request claim documents intact until TTL or owner-approved maintenance. Review artifact cleanup retention before deleting deployment images needed for rollback.
4. Verify deployed instance counts, versions and App Check. From an authorized test installation, run a synthetic translation and, if enabled, voice/History playback and learning generation. Temporarily set the relevant unit cap to the current reserved total to exercise cutoff. Verify the rejection and unchanged usage counter and provider telemetry, then restore the approved cap. Use a maintenance window because caps apply across accounts.
5. To pause provider dispatch, remove the affected meter from the current policy or set its `maxUnits` to zero. In-flight calls and already-issued Speech tokens can still run. If rollback requires restoring a prior backend revision, keep a provider-side bound in place before redeploying it. The earlier revision lacks the shared allowance. Roll back only the affected function IDs, verify mobile behavior and preserve usage records.
