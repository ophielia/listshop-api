Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.2.1] June 9, 2020


## [1.2.0] June 9, 2020

### Changed
*   ShoppingList api object changed
    -  ShoppingList returns sources in Legend_Sources. 
    -  Item api object now return source keys only in new field sourc_keys.

### Deprecated
* In ShoppingList api object
    -  Dish_sources and list_sources are depracated in ShoppingList api object
    -  Dish_sources and list_sources are depracated in Item api object

### Added
* new table user_devices to hold device information and token per device for user.
* logout method which removes device from db.

### Changed
* tokens generated on authentication are now persisted to the db.
* authentication verifies according to token saved in db.

### Fixed
* bug with tag_relationships having same child and parent corrected. 


## [1.1.1] - baseline
