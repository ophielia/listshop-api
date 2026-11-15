# Amounts - issues and thoughts

This document is a collection of thoughts and issues related to the handling of amounts for ListShop

## Issues

- **Plurals**: Will need to come up with a strategy for plurals
  - both plural tags and plural units - and when to use them
- **Fraction in Amount**: do we need this
  - currently the amount has a fractional string, but not the actual value
  - on paper, this is "missing" - but so far we haven't needed it.
- **Oversaving**: We're saving the entire amount in the `ListItemDetailEntity`
  - is this really necessary?
