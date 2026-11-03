# Tree-run gameplay evidence

These screenshots were supplied by the player from the October 30 run of the earlier build. They show bank preparation, progression through regular and fruit tree patches, and completion at Kastori. They are evidence of that gameplay run; the follow-up fixes were validated separately with automated tests.

## Bank preparation
![Quest Helper bank supplies and tree-run inventory](04-bank-preparation.png)

## Farming Guild
![Farming Guild fruit-tree step after planting and composting the regular tree](05-farming-guild.png)

## Nexus feedback
![Lumbridge instructions still show generic Nexus guidance](06-nexus-feedback.png)

The player reported missing remembered shortcuts after opening the Nexus. The follow-up accepts keys separated from destination text by whitespace and keys rendered in separate widgets on the same row, in addition to the existing colon format. It preserves per-character storage and invalidation when configuring the Nexus.

## Run completed at Kastori
![Wait-for-growth step, selected settings and planting/compost messages](07-run-completed.png)

The final screenshot shows the helper reaching “Wait for your trees to grow”, with chat messages for following the mountain guide to Quetzecalli Gorge, planting and composting the fruit tree and calquat.

## Follow-up fixes
- Refresh selected route instructions on the next game tick even when no config event arrives.
- Keep both Stronghold patches local from the spirit-tree arrival area; omit unnecessary teleport supplies for destinations already reached.
- Add Kastori travel choice `Mountain Guide Quetzal`. It uses the selected Nemus/Auburnvale arrival route (select POH Fairy Ring for AIS), then the mountain guide and nearby quetzal. Once at Auburnvale, it stops recommending a return to the house.
- Add regression tests for route changes, Nexus text/widget formats, Stronghold arrival, and continuation from Auburnvale.

Validation: [GitHub Actions run 41099902031](https://github.com/Jalawoud/quest-helper/actions/runs/41099902031) passed the farm-run test suite and built the runnable client for code commit `f55b3d4`. The new follow-up behavior still needs an in-game retest; the screenshots above predate those fixes.
