# What it means for a node to be a child of `is_vul`

## Background: the dual-direction construction

Each function pair P generates **two rows** in the feature matrix:

- `(features_AB, is_vul=False)` — Coming run in the fixing direction (vulnerable → patched)
- `(features_BA, is_vul=True)` — Coming run in the introducing direction (patched → vulnerable)

`is_vul` is not observed; it is assigned by whichever direction you ran Coming. It is a constructed label, not a measured phenomenon.

## Edge orientations relative to `is_vul`

In the example DAG (`bn1_hillclimb_mi10_hcs`):

- **Parents of `is_vul`** (predict the direction): `delete-node_If_Block`, `insert-node_If_Block`, `delete-node_Invocation_Block`
- **Children of `is_vul`** (predicted by the direction): `insert-node_Invocation_Block`, `insert-node_Invocation_LocalVariable`, `delete-node_Invocation_LocalVariable`

## What `is_vul → X` means

The BN factorizes X's distribution as `P(X | is_vul, other parents of X)`. This means: knowing the direction of the diff carries additional explanatory power for whether change X occurred, even after accounting for X's other parents.

Semantically: **these are directionally asymmetric changes whose asymmetry is not already explained by the other features connected to X.** Knowing "this is a fix" vs. "this is an introduction" still moves your belief about whether X occurred.

## Contrast with parents of `is_vul`

A **parent** of `is_vul` (e.g. `delete-node_If_Block → is_vul`) is a discriminator — seeing that change shifts your belief about which direction the diff ran. These are the core signals the model uses to tell fixing from introducing.

**Children are the opposite**: they are not independent predictors of direction. Once you know the direction (as explained by its parents), these changes are downstream consequences — they co-occur with fixing or introducing patterns, but their role in the model is as outcomes, not predictors.

For `insert-node_Invocation_LocalVariable` and `delete-node_Invocation_LocalVariable` specifically: invocation-in-local-variable changes tend to appear in one direction, but the model says they are not primary signals. Once you know the overall fixing/introducing context, they follow. They are **correlated accompaniments** of the direction rather than discriminative features of it.

## Causal caveat

Because `is_vul` is a constructed label, `is_vul → X` does not mean vulnerability causally produces X in the real world. It means: within this dataset, the direction variable explains these change types better than these change types explain the direction. The orientation is determined by the HCS score and the v-structure constraints imposed by the rest of the graph — not by any ground-truth causal claim.
