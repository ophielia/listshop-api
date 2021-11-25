# Changelog - The List Shop - API

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.0] November 2021

### Changed
 * beginning to multi-module in maven
 
### Deprecated
 * TagInfoRestController

### Fixed
 * Login in one browser doesn't kick out of other browsers

### Added
 * Task to clear exipired tokens

## [1.4.3] November 2021

### Fixed

- test fixes

## [1.4.2] October 2021

### Added

- new method in DishRestController which sets rating to a specific step: POST /dish/{dishId}/rating/{ratingId}/{step}

### Changed

- tests for ShoppingListService and TagService moved to mocks

### Deprecated

 - TagInfoController, previously used to accessed structured tag information, is now depracated. TagRestController method GET /tags with the option extended should be used instead to access the same information.

### Fixed
 
 - No longer possible to delete last list for user

## [1.4.1] July 2021

### Added

- created new custom repository to handle statistic creation and management

### Changed

- tests moved from db integration tests to mock tests

## [1.4.0] July 2021

### Changed

* Using testcontainers rather than local database for tests
* Moving some tests from IT tests to Mock tests

### Added

* sort parameters to endpoint GET /dish
* name fragment parameter to GET /dish

## [1.3.2] - June 2021

## Changed

- removed field injection from several services
- squashed flyway migrations

## [1.3.1] - April 2021

## Changed

- several services use constructor autowiring rather than field autowiring

## [1.3.0]  January 2, 2021

### Added

* new endpoint POST /shoppinglist/dish with ListAddProperties which adds multiple dishes to a lsit at the same time.
* workflow to repository to build and push docker image upon release.

### Changed

* Upgrade to Java 11
* Upgrade to spring boot 2.2.2.RELEASE

### Fixed
* bug in which crossed off items were not copied as crossed off
* bug in which updates to list items didn't take list or dish context into account.
* many small refactors

## [1.2.2]  August 1, 2020

### Added
* Configuration to serve static resources
  - from server directory /opt/com.meg.listshop.listshop/static
  - served from http://<root>/static
  
### Changed
* Exception AuthenticationException now returned if user 
token has expired

### Fixed
* bug in which RatingTagProcessor broke if no rating tags available
* bug in add from list.  UsedCount now correctly updated.


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

### Added

### Changed

### Deprecated

### Fixed