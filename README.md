Core Domain Plugin
=======================

## By CauseCode Technologies Pvt. Ltd.

* This plugin provides the functionality to store User's contact information.

## Basic Domain Hierarchy

* Contact: [Address, email, Phone]

* Address: [address, zip, City]

* City: [city, state, country]

* Country: [name, code, flag]

* Phone: [number, CountryCode]

* CountryCode: [code, Country]

## ChangeLog

### Version 0.4

#### Dotabase modifications

1. Moved latitude & longitude from **City** to **Location**.

### Version 0.3

#### Breaking changes

1. Changed plugin name to core-domain,
2. Domain packages changed.

#### Domain modifications

1. Changed constraints over various domains,
2. New Currency domain added.

#### Improvements

1. Added generic service to resolve parameters for contact & addresses & make them available to easily bind.