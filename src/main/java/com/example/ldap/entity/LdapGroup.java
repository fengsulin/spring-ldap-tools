package com.example.ldap.entity;

import com.example.ldap.constants.LdapConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.util.List;

@Data
@Accessors(chain = true)
@Entry(objectClasses = {LdapConstants.POSIX_GROUP})
public class LdapGroup {
    private static final long serialVersionUID = 3456L;

    @Id
    private Name dn;

    @Attribute(name = LdapConstants.CN)
    private String cn;

    @Attribute(name = LdapConstants.GID_NUMBER)
    private String gidNumber;

    @Attribute(name = LdapConstants.MEMBER_UID)
    private List<String> memberUid;
}
