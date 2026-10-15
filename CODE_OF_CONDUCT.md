# Group16-DevNexus Code of Conduct

## 1. Purpose, Scope and Professional Ethics

This Code of Conduct defines the standards of collaboration for the six members
of Group16-DevNexus throughout the SET09803 DevOps coursework. It applies to all
members equally across meetings, team communication, GitHub, code reviews and
project delivery. It supplements the module assessment procedures and University
regulations.

- **Respectful collaboration:** Communicate professionally, listen to different
  viewpoints and discuss technical disagreements calmly. Harassment,
  discrimination, bullying, threats and personal attacks are unacceptable.
- **Early communication:** Report blockers, likely delays and availability changes
  promptly so that the team can adjust its sprint plan.
- **Academic integrity:** Credit contributions honestly. Do not fabricate commits,
  test results, screenshots, task completion or contribution records.
- **Privacy:** Keep account credentials and private personal information out of
  public issues, pull requests and Wiki pages.

## 2. Team Roles, Work Distribution and Definition of Done

### Team Members and Responsibilities

| Member | GitHub Account | Role | Assigned Work |
|---|---|---|---|
| Han Htal Sein | PiKayz | Scrum Master / Developer / Repository Controller | Population breakdowns 23–25; database connection DB01; integration and final merge coordination |
| Khaine Zaw Hein | KhaineZawHein | Product Owner / Developer | Population totals 26–31; backlog prioritisation and acceptance review |
| Htet Aung Shine | htetaungshine572005-pixel | Developer | Country reports 1–6 |
| Htet Aung Lin | 40870386-HtetAungLin | Developer | All-city reports 7–11 |
| Hein Phyo | Fineshit-fys | Developer | Capital city reports 17–22 |
| Win Aung | wina23490-spec | Developer | Top N city reports 12–16; language report 32 |

### Scrum Responsibilities

- **Product Owner:** Communicate the product vision, prioritise the backlog and
  check user stories and acceptance criteria against the assessment.
- **Scrum Master:** Facilitate planning and communication, help remove blockers,
  mediate disagreements and coordinate the Sprint and Kanban workflow.
- **Developers:** Implement assigned reports, write suitable tests and comments,
  maintain use cases and evidence, and participate in peer review.
- **Repository Controller:** Han coordinates integration and final merges while
  respecting the repository's required approval and branch protection rules.

### Fair Work Distribution

Allocate tasks according to workload, skills and availability. Review the
allocation during sprint planning and adjust it when needed. Report counts are
not contribution percentages: implementation, testing, documentation, reviews,
troubleshooting and coordination all count as contributions.

### Definition of Done

A task is Done only when:

1. Its agreed acceptance criteria are satisfied.
2. Relevant unit or integration tests and manual checks are completed, with
   results recorded. Documentation-only tasks use appropriate review checks.
3. The application builds and affected Docker/CI checks pass.
4. A pull request links the work to its issue and describes validation.
5. Another eligible team member reviews and approves the work; review
   conversations are resolved.
6. Han merges the reviewed work into develop through the PR process.
7. Output evidence, documentation and the Project status are updated.

The author cannot approve their own PR. When Han authors a PR, another eligible
collaborator supplies the required approval.

## 3. Meetings and Sprint Planning

Meeting days, times and channels must be agreed by all members and recorded in
team meeting notes. Times use Myanmar time (Asia/Rangoon, UTC+06:30).

| Meeting | Timing | Purpose |
|---|---|---|
| Sprint planning | At the start of each sprint; exact time agreed by the team | Agree priorities, owners, acceptance criteria and workload |
| Progress and blocker check | Agreed during sprint planning | Review progress and arrange support |
| Sprint review and retrospective | At the end of each sprint; exact time agreed by the team | Demonstrate work and improve collaboration |
| Assessed code reviews | Time allocated by the module teaching team | Present coursework evidence and answer questions |

| Sprint | Start Date | End Date |
|---|---|---|
| Sprint 1 | 08 October 2026 | 11 October 2026 |
| Sprint 2 | 12 October 2026 | 14 October 2026 |
| Sprint 3 | 15 October 2026 | 31 October 2026 |
| Sprint 4 | 01 November 2026 | 20 November 2026 |

Notify the team as early as possible if attendance is affected. University code
review attendance penalties are governed by the assessment, not by this team policy.

## 4. Operational Rules and GitFlow Standards

- **Feature branches:** Work on the assigned branch. Htet Aung Lin uses
  feature/city-reports-htet-aung-lin; Win Aung uses feature/city-reports-win-aung
  for his city reports. Coordinate changes to shared Java files during integration.
- **Protected branches:** Do not push changes directly to main, develop or release.
  Use reviewed pull requests and follow repository protection rules.
- **Assessment branch:** The supplied assessment refers to master, while this
  repository uses main. Confirm the accepted final submission branch with the
  teaching team before submission.
- **Work tracking:** Link work to GitHub Issues and keep backlog, sprint and Kanban
  items current. Maintain any Zube.io integration required by the assessment.
- **Communication:** Aim to acknowledge project messages within 24 hours during
  agreed availability. Notify the team of planned absence; consider illness,
  emergencies, work commitments and time zones before treating a delay as a breach.
- **Code quality:** Use meaningful commits, appropriate comments, suitable tests
  and reproducible Maven and Docker builds. Do not overwrite another member's work.
- **Evidence:** Mark unfinished reports honestly. Do not present CI success alone
  as proof that all requirements have been met.

## 5. Breaches, Corrective Actions and Escalation

Consequences should be proportionate, evidence-based and directed toward resolving
problems. Members must be able to explain their circumstances without retaliation.

| Breach | Initial Corrective Action | Response if Repeated or Unresolved |
|---|---|---|
| Meeting lateness without notice | Discuss the reason and agree notification expectations | Written warning and attendance improvement plan |
| Unexplained absence | Contact the member privately and consider mitigation | Written warning and agreed participation plan |
| Repeated communication delays | Agree realistic response times and a suitable channel | Review communication arrangements and record an improvement plan |
| Incomplete sprint task | Identify blockers, provide support and revise the delivery plan | Record evidence and review workload or task ownership |
| Unclear contribution evidence | Review code, tests, documents, issues and review work together | Refer an unresolved contribution dispute to the teaching team |
| Unauthorised repository changes | Review and repair the affected changes through the normal workflow | Written warning; escalate serious or repeated incidents |
| Harassment, dishonesty or deliberate damage | Preserve evidence and protect affected members | Refer serious concerns directly to the module teaching team |

### Progressive Procedure

1. **Friendly reminder:** Discuss a minor first breach privately and agree corrective action.
2. **Written warning:** Record a repeated concern, the member's response and the expected improvement.
3. **Improvement plan:** Agree reasonable actions, support and a review date.
4. **Teaching-team referral:** Present evidence of serious or unresolved concerns.
   Serious incidents may be referred immediately without earlier stages.

The group does not impose financial fines, compulsory purchases, automatic mark
deductions or arbitrary contribution-percentage penalties. Additional work must
be fairly agreed during planning rather than imposed as punishment.

### Contribution Assessment

Contribution percentages must reflect actual work and be agreed by the team.
They must total 100% at each assessment point. Commit counts alone are insufficient:
reviews, tests, documentation and coordination must also be considered. If the
team cannot agree, or a member disputes the record, contact the module teaching team.

### Dismissal and Appeal

The supplied assessment requires evidence of a Code of Conduct breach to be
presented to the module delivery team. The member concerned may provide mitigating
circumstances to the group or privately to the teaching team. Only the module
delivery team makes the final decision on whether dismissal is warranted.

The assessment states that a dismissed member cannot deliver the group coursework
and will fail. A dismissed member has one week to appeal to the module team with
supporting evidence. The group cannot independently dismiss a member or change
academic marks.

## 6. Reporting Concerns and Policy Agreement

Report concerns privately to Han Htal Sein or Khaine Zaw Hein, with relevant dates
and factual evidence. If either is involved, or the matter is serious, contact the
module teaching team directly. Do not publish personal complaints or sensitive
evidence in public project areas.

All six members should review this policy and record agreement in meeting notes
or another agreed team record. Agreement is not assumed from adding this file.
Discuss changes with the team, submit them through a reviewed PR and keep the
Wiki summary consistent with the final policy.

## Penalty Matrix

| Breach | First Occurrence | Repeated or Unresolved Breach |
|---|---|---|
| Meeting lateness exceeding 5 minutes without notice | Friendly reminder and discussion of the reason | Written warning and agreed attendance improvement plan |
| Unexplained meeting absence | Private follow-up and opportunity to explain | Written warning and participation improvement plan |
| No response within 24 hours during agreed availability | Reminder and check for communication difficulties | Agree revised communication arrangements and a review date |
| Incomplete sprint task without an early progress update | Review blockers, provide support and revise the deadline | Written improvement plan and agreed task reallocation |
| Missing contribution evidence | Review commits, issues, tests, documentation and peer reviews | Refer an unresolved contribution dispute to the teaching team |
| Unauthorised changes to another member's work | Review the impact and agree how to repair the changes | Written warning; escalate serious or repeated incidents |
| Attempting to bypass branch or PR rules | Explain the required workflow and correct the action | Written warning and referral if the behaviour continues |
| Missing an assessed code review | Inform the teaching team and provide mitigating circumstances | Follow the module assessment procedures |
| Harassment, fabricated evidence or deliberate damage | Preserve evidence and refer serious concerns directly to the teaching team | Teaching team determines further action |

### Fair Enforcement

- Give the member an opportunity to explain and respond.
- Consider illness, emergencies, work commitments and time zones.
- Record concerns and agreed actions privately.
- Do not impose automatic mark deductions, fines or compulsory purchases.
- Contribution percentages must reflect actual work and be agreed by the team.
- Only the module teaching team can decide dismissal or academic consequences.
- Under the assessment procedure, a dismissed member has one week to appeal.

