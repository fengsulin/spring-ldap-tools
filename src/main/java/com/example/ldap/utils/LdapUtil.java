package com.example.ldap.utils;

import com.example.ldap.constants.LdapConstants;
import com.example.ldap.entity.LdapGroup;
import com.example.ldap.entity.LdapPerson;
import com.example.ldap.entity.LdapUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.naming.Name;
import javax.naming.directory.*;
import javax.naming.ldap.LdapName;
import java.util.List;


/**
 * openLDAP工具类
 */
@Component
@Slf4j
public class LdapUtil {
    private final LdapTemplate ldapTemplate;

    public LdapUtil(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    /**
     * ldap 用户认证
     * @param username
     * @param password
     * @return boolean
     */
    public boolean authenticate(final String username,final String password){
        EqualsFilter filter = new EqualsFilter(LdapConstants.UID, username);
        try{
            boolean authenticate = ldapTemplate.authenticate(LdapUtils.emptyLdapName(), filter.toString(), password);
            if (authenticate){
                log.info("用户["+username+"]认证成功");
                return true;
            }
            log.warn("用户["+username+"]认证失败");
            return false;
        }catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException("ldap认证异常");
        }
    }

    /**
     * 通过用户名查询用户信息
     * @param username
     * @return LdapPerson
     */
    public LdapPerson findByUsername(final String username){
        // 构建查询builder
        LdapQueryBuilder builder = LdapQueryBuilder.query();
        builder.where(LdapConstants.UID).is(username);
        LdapPerson ldapPerson = ldapTemplate.findOne(builder,LdapPerson.class);
        log.info("查询用户["+username+"]信息：{}",ldapPerson);
        return ldapPerson;
    }

    /**
     * 获取ldap所有用户
     * @return
     */
    public List<LdapPerson> findUserAll(){
        List<LdapPerson> ldapPersonList = ldapTemplate.findAll(LdapPerson.class);
        if (ldapPersonList != null) log.info("查询所有ldap用户，总数为：{}",ldapPersonList.size());
        return ldapPersonList;
    }


    /**
     * 创建ldap用户（注意：用些属性需要特定的objectclass，否则会报错）
     * @param ldapPerson 用户基本信息
     * @return
     */
    public boolean createUser(final LdapPerson ldapPerson) {
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute(LdapConstants.OBJECT_CLASS);
        attribute.add(LdapConstants.INET_ORG_PERSON);
        attribute.add(LdapConstants.POSIX_ACCOUNT);
        attribute.add(LdapConstants.TOP);
        attributes.put(attribute);

        attributes.put(LdapConstants.CN,ldapPerson.getCn());
        attributes.put(LdapConstants.GID_NUMBER,ldapPerson.getGidNumber());
        attributes.put(LdapConstants.UID_NUMBER,ldapPerson.getUidNumber());
        attributes.put(LdapConstants.UID,ldapPerson.getUid());
        attributes.put(LdapConstants.SN, ldapPerson.getSn());

        attributes.put(LdapConstants.USER_PASSWORD,ldapPerson.getPassword());
        attributes.put(LdapConstants.MAIL,ldapPerson.getEmail());
        attributes.put(LdapConstants.MOBILE,ldapPerson.getMobile());
        attributes.put(LdapConstants.HOME_DIRECTORY,"/home/users/"+ldapPerson.getUid());
        ldapTemplate.bind("cn="+ldapPerson.getUid()+",ou=users",null,attributes);
        return true;
    }

    /**
     * 更新ldap用户某条字段
     * @param ou
     * @param cn
     * @param filedName
     * @param filedValue
     * @return
     */
    public boolean updateUser(String ou,String cn,String filedName,String filedValue){
        // 构建LdapName-》dn，用以唯一定位用户
        LdapName ldapName = null;
        if (StringUtils.hasText(ou)){
            ldapName = LdapNameBuilder.newInstance().add(LdapConstants.OU, ou).add(LdapConstants.CN,cn).build();
        }else {
            ldapName = LdapNameBuilder.newInstance().add(LdapConstants.CN,cn).build();
        }
        Attribute attr = new BasicAttribute(filedName, filedValue);
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        ldapTemplate.modifyAttributes(ldapName,new ModificationItem[]{item});
        return true;
    }

    /**
     * 修改ldap用户信息
     * @param ldapPerson
     * @return
     */
    public boolean updateUser(final LdapPerson ldapPerson){
       if (ldapPerson.getDn() == null) return false;

       // 这里暂时只修改附属的属性
       ldapTemplate.modifyAttributes(ldapPerson.getDn(), new ModificationItem[]{
               new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(LdapConstants.MAIL,ldapPerson.getEmail())),
               new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(LdapConstants.MOBILE,ldapPerson.getMobile())),
               new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(LdapConstants.UID,ldapPerson.getUid()))
       });
       return true;
    }

    /**
     * 删除ldap用户
     * @param dn
     * @return
     */
    public boolean deleteUser(final Name dn){
        if (dn == null) return false;
        ldapTemplate.unbind(dn);
        return true;
    }

    /**
     * 删除ldap用户
     * @param dn
     * @return
     */
    public boolean deleteUser(final String dn){
        if (StringUtils.hasText(dn)){
            ldapTemplate.unbind(dn);
            return true;
        }
        return false;
    }

    /**
     * 创建组织单元
     * @param ldapUnit
     * @return
     */
    public boolean createLdapUnit(final LdapUnit ldapUnit){
        BasicAttributes attributes = new BasicAttributes();
        BasicAttribute attribute = new BasicAttribute(LdapConstants.OBJECT_CLASS);
        attribute.add(LdapConstants.ORGANIZATIONAL_UNIT);
        attribute.add(LdapConstants.TOP);

        attributes.put(attribute);
        attributes.put(LdapConstants.OU,ldapUnit.getOu());

        ldapTemplate.bind(ldapUnit.getDn(), null,attributes);
        return true;
    }

    /**
     * 创建ldap组
     * @param ldapGroup
     * @return
     */
    public boolean createGroup(final LdapGroup ldapGroup){
        BasicAttributes attributes = new BasicAttributes();
        BasicAttribute attribute = new BasicAttribute(LdapConstants.OBJECT_CLASS);
        attribute.add(LdapConstants.POSIX_GROUP);
        attribute.add(LdapConstants.TOP);

        attributes.put(attribute);
        attributes.put(LdapConstants.CN,ldapGroup.getCn());
        attributes.put(LdapConstants.GID_NUMBER,ldapGroup.getGidNumber());
        ldapTemplate.bind(ldapGroup.getDn(), null,attributes);
        return true;
    }

    /**
     * 查询ldap组信息
     * @param cn
     * @return group
     */
    public LdapGroup findGroup(final String cn){
        LdapQueryBuilder query = LdapQueryBuilder.query();
        query.where(LdapConstants.CN).is(cn);
        LdapGroup group = ldapTemplate.findOne(query, LdapGroup.class);
        log.info("查询组["+cn+"]信息为:{}",group);
        return group;
    }

    /**
     * 查询所有组
     * @return groups
     */
    public List<LdapGroup> findAllGroup(){
        List<LdapGroup> groups = ldapTemplate.findAll(LdapGroup.class);
        log.info("查询所有组信息为:{}",groups);
        return groups;
    }

    /**
     * 新增成员到组
     * @param groupDn
     * @param userDn
     * @return
     */
    public boolean addUserToGroup(final String groupDn,final String userDn){
        DirContextOperations ctxGroup = ldapTemplate.lookupContext(groupDn);
        DirContextOperations ctxUser = ldapTemplate.lookupContext(userDn);
        ctxGroup.addAttributeValue(LdapConstants.MEMBER_UID,ctxUser.getStringAttribute(LdapConstants.CN));
        ldapTemplate.modifyAttributes(ctxGroup);
        return true;
    }

    /**
     * 移除group中成员
     * @param groupDn
     * @param userDn
     * @return
     */
    public boolean removeUserFromGroup(final String groupDn,final String userDn){
        DirContextOperations ctxGroup = ldapTemplate.lookupContext(groupDn);
        DirContextOperations ctxUser = ldapTemplate.lookupContext(userDn);
        ctxGroup.addAttributeValue(LdapConstants.MEMBER_UID,ctxUser.getStringAttribute(LdapConstants.CN));
        ldapTemplate.modifyAttributes(ctxGroup);
        return true;

    }
}
