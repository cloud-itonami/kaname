# kaname - Constitutional Core Actor

**DID**: `did:web:kaname.etzhayyim.com`
**Namespace**: `com.etzhayyim.kaname.*`
**Status**: migration boundary for constitutional, membership, land, tithe, and treasury cells

## Migration Boundary

`src/kaname/murakumo.cljc` is the Murakumo-facing cljc actor boundary for the
legacy kaname-related kotoba-kotodama cells:

- `charter_attestation_request` -> `charterAttestationReview`
- `ethics_content_classifier` -> `ethicsContentJudgment`
- `land_donation_processing` -> `landDonationReview`
- `member_registry` -> `memberRegistryCertificate`
- `religious_corp_taxation` -> `taxAuditView`
- `religious_marriage` -> `religiousMarriageRecord`
- `tithe_routing` -> `titheAuditRecord`
- `treasury_rebalance` -> `treasuryRebalanceProposal`

The actor plans constitutional records only. It does not directly move funds,
discharge state tax obligations, replace civil registry or civil marriage, invent
land claims, or bypass Council and legal-counsel gates.
