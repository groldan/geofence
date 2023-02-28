# GeoFence Domain Business Model

## Dependency graph

```mermaid
flowchart LR
  subgraph org.geoserver.geofence.domain
    direction LR
    rule-management
    adminrule-management --> rule-management
    user-management
    authorization --> adminrule-management & user-management
  end
```

## Component diagram


```mermaid
C4Context
  Container_Boundary(b, "Core domain components") {
    Boundary(rmb, "Rule Management") {
      Component(rule, "Rule Admin Service", "", "Manages data access rules")
      
      Component(ruleRepo, "Rule Repository", "Repository", "Rule Domain Persistence Abstraction")
      
      Rel_R(rule, ruleRepo, "", "Rule")
    }
    Boundary(armb, "Admin Rule Management") {
      Component(adminRule, "AdminRule Admin Service", "Spring Bean")
      
      Component(adminRuleRepo, "AdminRule Repository", "Repository", "AdminRule Domain Persistence Abstraction")
      
      Rel_R(adminRule, adminRuleRepo, "", "AdminRule")
    }

    Boundary(authb, "Authorization") {
      Component(ruleAcces, "Rule Reader Service", "", "Merges several data access rules into a single set of access limits, based on a rule filter")
      
      Component(rolesResolver, "User Role names Resolver", "Java Function", "Resolves the set of Role names a user belongs to based on the user name")
      
      Component(auth, "User Authorization Service", "", "Used by GeoServer to authenticate users")
      
      Rel_R(auth, user, "Obtains user to compare credentials")
      Rel_R(ruleAcces, adminRule, "")
      Rel_R(ruleAcces, rule, "")
      Rel_R(ruleAcces, rolesResolver, "gets user roles from")
    }

    Boundary(umb, "User Management") {
      Component(user, "User Admin Service", "", "")
      
      Component(userRepo, "User Repository", "Repository", "GeoServerUser Domain Persistence Abstraction")
      
      Component(group, "UserGroup Admin Service", "", "")
      
      Component(groupRepo, "UserGroup Repository", "", "GeoServerUserGroup Domain Persistence Abstraction")
      
      Rel_R(user, userRepo, "", "GeoServerUser")
      Rel_R(group, groupRepo, "", "GeoServerUserGroup")
    }
  }
```
