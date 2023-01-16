package com.example.ldap.utils;

import com.example.ldap.entity.LdapGroup;
import com.example.ldap.entity.LdapPerson;
import com.example.ldap.entity.LdapUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.support.LdapNameBuilder;

import java.util.List;

@SpringBootTest
class LdapUtilsTest {

    private String username = "zhangsan";
    private String password = "123456";
    @Autowired
    private LdapUtil ldapUtil;
    @Test
    void authenticate() {
        boolean authenticate = ldapUtil.authenticate(username, password);
        Assertions.assertTrue(authenticate,"认证失败");
    }

    @Test
    void findByUsername(){
        LdapPerson ldapPerson = ldapUtil.findByUsername(username);
        Assertions.assertNotNull(ldapPerson,"查询失败");
    }

    @Test
    void findAll(){
        List<LdapPerson> ldapPersonList = ldapUtil.findUserAll();
        Assertions.assertNotNull(ldapPersonList,"查询ldap失败");
    }

    @Test
    void createLdapPerson(){
        LdapPerson ldapPerson = new LdapPerson();
        ldapPerson.setCn("wuhan")
                .setEmail("wuhan@test.com")
                .setSn("wuhan")
                .setMobile("12343223456")
                .setUid("wuhan")
                .setGidNumber("31893")
                .setUidNumber(System.currentTimeMillis()+"")
                .setPassword("123456");
        ldapUtil.createUser(ldapPerson);
    }

    @Test
    void updateLdapPerson(){
        ldapUtil.updateUser("users","wuhan","mobile","12343223457");
    }

    @Test
    void updateLdapPersonTest(){
        LdapPerson ldapPerson = new LdapPerson();
        ldapPerson.setDn(LdapNameBuilder.newInstance("cn=songlei,ou=users").build())
                .setEmail("123@test.com")
                .setUid("henan")
                .setMobile("12323456754");
        boolean b = ldapUtil.updateUser(ldapPerson);
        Assertions.assertTrue(b,"更新失败");
    }

    @Test
    void deleteLdapPerson(){
        boolean b = ldapUtil.deleteUser(LdapNameBuilder.newInstance("cn=songlei,ou=users").build());
        Assertions.assertTrue(b,"删除失败");
    }

    @Test
    void createLdapUnit(){
        LdapUnit ldapUnit = new LdapUnit();
        ldapUnit.setDn(LdapNameBuilder.newInstance("ou=devs").build());
        ldapUnit.setOu("devs");
        boolean ldapUnit1 = ldapUtil.createLdapUnit(ldapUnit);
        Assertions.assertTrue(ldapUnit1,"创建失败");
    }

    @Test
    void findGroup(){
        LdapGroup group = ldapUtil.findGroup("hlwyys-cntm");
        Assertions.assertNotNull(group,"查询失败");
    }

    @Test
    void findAllGroup(){
        List<LdapGroup> allGroup = ldapUtil.findAllGroup();
        Assertions.assertNotNull(allGroup);
    }

    @Test
    void createGroup(){
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setDn(LdapNameBuilder.newInstance("cn=ccs,ou=devs").build())
                .setCn("ccs")
                .setGidNumber("28456");
        boolean group = ldapUtil.createGroup(ldapGroup);
        Assertions.assertTrue(group);
    }

    @Test
    void addUserToGroup(){
        boolean b = ldapUtil.addUserToGroup(" cn=dds,ou=devs", "cn=wangwu,ou=devops");
        Assertions.assertTrue(b);
    }

    @Test
    void removeUserFromGroup(){
        boolean b = ldapUtil.removeUserFromGroup(" cn=dds,ou=devs", "cn=wangwu,ou=devops");
        Assertions.assertTrue(b);
    }
}