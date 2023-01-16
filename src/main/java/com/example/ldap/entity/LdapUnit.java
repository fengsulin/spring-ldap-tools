package com.example.ldap.entity;

import com.example.ldap.constants.LdapConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;

@Entry(objectClasses = {LdapConstants.ORGANIZATIONAL_UNIT})
@Data
@Accessors(chain = true)
public class LdapUnit implements Serializable {
    private static final long serialVersionUID = 345L;

    @Id
    private Name dn;

    @Attribute(name = LdapConstants.OU)
    private String ou;
}
