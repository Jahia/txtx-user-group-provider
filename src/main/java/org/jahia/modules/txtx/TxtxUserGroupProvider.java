package org.jahia.modules.txtx;

import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.StringUtils;
import org.jahia.modules.external.users.*;
import org.jahia.services.usermanager.JahiaGroup;
import org.jahia.services.usermanager.JahiaGroupImpl;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserImpl;

import javax.jcr.RepositoryException;
import java.util.*;
import java.util.regex.Pattern;

public class TxtxUserGroupProvider implements UserGroupProvider {

    public static final String TXTX_PROVIDER_KEY = "txtx";

    private List<String> users = Arrays.asList("tata", "tete", "titi");

    private List<String> groups = Arrays.asList("toto", "tutu", "tyty");

    private ExternalUserGroupService externalUserGroupService;

    @Override
    public JahiaUser getUser(String name) throws UserNotFoundException {
        if (users.contains(name)) {
            Properties properties = new Properties();
            properties.put("j:email", "mail@tx.tx");
            return new JahiaUserImpl(name, name, properties, false, TXTX_PROVIDER_KEY);
        }
        throw new UserNotFoundException("Cannot find user " + name);
    }

    @Override
    public JahiaGroup getGroup(String name) throws GroupNotFoundException {
        if (groups.contains(name)) {
            Properties properties = new Properties();
            return new JahiaGroupImpl(name, name, null, properties);
        }
        throw new GroupNotFoundException("Cannot find group " + name);
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
        if ("tyty".equals(groupName)) {
            members.add(new Member("tete", Member.MemberType.USER));
            members.add(new Member("titi", Member.MemberType.USER));
        }
        return members;
    }

    @Override
    public List<String> getMembership(Member member) {
        if ("tata".equals(member.getName())) {
            return Arrays.asList("toto");
        } else if ("tete".equals(member.getName())) {
            return Arrays.asList("toto", "tyty");
        } else if ("titi".equals(member.getName())) {
            return Arrays.asList("tutu", "tyty");
        } else if ("toto".equals(member.getName())) {
            return Collections.emptyList();
        } else if ("tutu".equals(member.getName())) {
            return Arrays.asList("toto");
        } else if ("tyty".equals(member.getName())) {
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public List<String> searchUsers(Properties searchCriterias, long offset, long limit) {
        return search(users, searchCriterias, "username", offset, limit);
    }

    @Override
    public List<String> searchGroups(Properties searchCriterias, long offset, long limit) {
        return search(groups, searchCriterias, "groupname", offset, limit);
    }

    private static List<String> search(List<String> allRows, Properties searchCriterias, String nameColumn, long offset, long limit) {

        // Return empty result in case there is an unknown search criteria.
        for (Map.Entry<?, ?> entry : searchCriterias.entrySet()) {
            Object name = entry.getKey();
            Object value = entry.getKey();
            if (!(name.equals(nameColumn) || name.equals("*") || value.equals("") || value.equals("*"))) {
                return Collections.emptyList();
            }
        }

        String filter = (String) searchCriterias.get(nameColumn);
        if (filter == null) {
            filter = (String) searchCriterias.get("*");
        }
        ArrayList<String> found;
        if (filter != null) {
            found = new ArrayList<String>(Collections2.filter(allRows, Predicates.contains(Pattern.compile("^" + StringUtils.replace(filter, "*", ".*") + "$"))));
        } else {
            found = new ArrayList<String>(allRows);
        }
        return found.subList(Math.min((int) offset, found.size()), limit < 0 ? found.size() : Math.min((int) (offset + limit), found.size()));
    }

    @Override
    public boolean verifyPassword(String userName, String userPassword) {
        return "password".equals(userPassword);
    }

    @Override
    public boolean supportsGroups() {
        return true;
    }

    @Override
    public boolean isAvailable() throws RepositoryException {
        return true;
    }

    public void init() {
        externalUserGroupService.register("txtx", this);
    }

    public void destroy() {
        externalUserGroupService.unregister("txtx");
    }

    public void setExternalUserGroupService(ExternalUserGroupService externalUserGroupService) {
        this.externalUserGroupService = externalUserGroupService;
    }
}
