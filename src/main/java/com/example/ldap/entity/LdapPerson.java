package com.example.ldap.entity;

import com.example.ldap.constants.LdapConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.io.Serializable;

@Data
@Entry(objectClasses = {LdapConstants.INET_ORG_PERSON})
@Accessors(chain = true)
public class LdapPerson implements Serializable {
    private static final long serialVersionUID = -134694L;

    @Id
    private Name dn;

    @Attribute(name = LdapConstants.BUSINESS_CATEGORY)
    private String businessCategory;

    @Attribute(name = LdapConstants.CN)
    private String cn;

    @Attribute(name = LdapConstants.MAIL)
    private String email;

    @Attribute(name = LdapConstants.GID_NUMBER)
    private String gidNumber;

    @Attribute(name = LdapConstants.MOBILE)
    private String mobile;

    @Attribute(name = LdapConstants.UID)
    private String uid;

    @Attribute(name = LdapConstants.SN)
    private String sn;

    private String password;

    @Attribute(name = LdapConstants.UID_NUMBER)
    private String uidNumber;
}
