package org.jahia.modules.txtx;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.external.users.ExternalUserGroupService;
import org.jahia.modules.external.users.Member;
import org.jahia.modules.external.users.UserGroupProvider;
import org.jahia.modules.external.users.UserNotFoundException;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserImpl;
import org.jahia.services.usermanager.JahiaUserManagerService;

import java.util.*;
import java.util.regex.Pattern;

public class TxtxUserGroupProvider implements UserGroupProvider {

    public static final String TXTX_PROVIDER_KEY = "txtx";

    private List<String> users = Arrays.asList("tata", "tete", "titi");

    private List<String> groups = Arrays.asList("toto", "tutu");

    private ExternalUserGroupService externalUserGroupService;

    @Override
    public JahiaUser getUser(String name) throws UserNotFoundException {
        if (users.contains(name)) {
            Properties properties = new Properties();
            properties.put("j:password", JahiaUserManagerService.encryptPassword("password"));
            return new JahiaUserImpl(name, name, properties, false, TXTX_PROVIDER_KEY);
        }
        throw new UserNotFoundException("Cannot find user " + name);
    }

    @Override
    public boolean groupExists(String name) {
        return groups.contains(name);
    }

    @Override
    public List<Member> getGroupMembers(String groupName) {
        ArrayList<Member> members = new ArrayList<Member>();
        if ("toto".equals(groupName)) {
            members.add(new Member("tata", Member.MemberType.USER));
            members.add(new Member("tete", Member.MemberType.USER));
            members.add(new Member("tutu", Member.MemberType.GROUP));
        }
        if ("tutu".equals(groupName)) {
            members.add(new Member("titi", Member.MemberType.USER));
        }
        return members;
    }

    @Override
    public List<String> getMembership(String principalName) {
        return null;
    }

    @Override
    public List<String> searchUsers(Properties searchCriterias) {
        String filter = (String) searchCriterias.get("username");
        if (filter != null) {
            return new ArrayList<String>(Collections2.filter(users, Predicates.contains(Pattern.compile("^" + StringUtils.replace(filter, "*", ".*") + "$"))));
        }
        filter = (String) searchCriterias.get("*");
        if (filter != null) {
            return new ArrayList<String>(Collections2.filter(users, Predicates.contains(Pattern.compile("^" + StringUtils.replace(filter, "*", ".*") + "$"))));
        }
        return new ArrayList<String>();
    }

    @Override
    public List<String> searchGroups(Properties searchCriterias) {
        String filter = (String) searchCriterias.get("groupname");
        if (filter != null) {
            return new ArrayList<String>(Collections2.filter(groups, Predicates.contains(Pattern.compile("^" + StringUtils.replace(filter, "*", ".*") + "$"))));
        }
        filter = (String) searchCriterias.get("*");
        if (filter != null) {
            return new ArrayList<String>(Collections2.filter(groups, Predicates.contains(Pattern.compile("^" + StringUtils.replace(filter, "*", ".*") + "$"))));
        }
        return new ArrayList<String>();
    }

    public void init() {
        externalUserGroupService.register("txtx", this);
    }

    public void setExternalUserGroupService(ExternalUserGroupService externalUserGroupService) {
        this.externalUserGroupService = externalUserGroupService;
    }
}
