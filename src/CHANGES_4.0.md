
## Persistence

Ported all Hibernate entity annotations to `javax.persistence.*` JPA
Made all root entities auditable

## User management

- `UserAdminService` refactored from interface to concrete class, delegating to `GeoServerUserReposiroty`
- Moved pwd encoding/decoding from `GSUser` to service layer (`UserAdminService`)


#TODO

## Additional Admin/Rule properties

- `ExtId` can be used to track rules produced by external systems.
- `name` can be used to identify rules in a human friendly way.
- `description`

## GeoServerUser password encoding

In 3.6, `GSUser` password is encoded and decoded in the setter and
getter respectively. Hence, `AuthorizationService` checks for equality
of the unencoded password. I'd be better if:
- The password is not part of the (api) object model
- A non-reversible hash was used to store the password
- `AuthorizationService` checks for equality of the hashed values
