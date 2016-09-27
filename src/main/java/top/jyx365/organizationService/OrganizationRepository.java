package top.jyx365.organizationService;

import java.util.List;
import javax.naming.Name;
import javax.naming.directory.SearchControls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapNameBuilder;
import static org.springframework.ldap.query.LdapQueryBuilder.query;
public class OrganizationRepository {
    @Autowired
    private LdapTemplate ldapTemplate;

    private void addNode(Name parentNode, String type) {
        Name dn = LdapNameBuilder.newInstance(parentNode)
            .add("ou",type)
            .build();
        DirContextAdapter context= new DirContextAdapter(dn);
        context.setAttributeValues("objectclass", new String[] {"top", "organizationalUnit"});
        context.setAttributeValue("ou", type);
        context.setAttributeValue("description", type);
        ldapTemplate.bind(context);

    }

    /*Company*/
    public List<Company> getCompanies() {
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        return ldapTemplate.findAll(null,sc,Company.class);
    }

    public void addCompany(Company company) {
        ldapTemplate.create(company);
        addNode(company.getId(),"departments");
        addNode(company.getId(),"staffs");
        addNode(company.getId(),"groups");
    }

    public Company findCompany(String companyId) {
        Name dn = LdapNameBuilder.newInstance(companyId).build();
        return ldapTemplate.findByDn(dn, Company.class);
    }


    /*departments*/
    public Department findDepartment(String departmentId) {
        Name dn = LdapNameBuilder.newInstance(departmentId).build();
        return ldapTemplate.findByDn(dn, Department.class);
    }

    public List<Department> findDepartments(String companyId) {
        SearchControls sc = new SearchControls();
        Name dn = LdapNameBuilder.newInstance(companyId)
            .add("ou","departments")
            .build();
        return ldapTemplate.findAll(dn,sc,Department.class);
    }

    public List<Department> findSubDepartments(String parent) {
        SearchControls sc = new SearchControls();
        Name dn = LdapNameBuilder.newInstance(parent).build();
        return ldapTemplate.findAll(dn,sc,Department.class);
    }

    public void addDepartment(Department dept) {
        ldapTemplate.create(dept);
    }

    /*department role*/
    public Role findRole(String roleId) {
        Name dn = LdapNameBuilder.newInstance(roleId).build();
        return ldapTemplate.findByDn(dn, Role.class);
    }

    public List<Role> findDepartmentRoles(String department) {
        SearchControls sc = new SearchControls();
        Name dn = LdapNameBuilder.newInstance(department).build();
        return ldapTemplate.findAll(dn, sc, Role.class);
    }

    public List<Role> findAllRoles(String companyId) {
        SearchControls sc = new SearchControls();
        Name dn = LdapNameBuilder.newInstance(companyId).build();
        return ldapTemplate.find(
                query().base(dn)
                    .searchScope(SearchScope.SUBTREE)
                    .where("objectClass").is("organizationalRole"),
                Role.class
                );
    }


    public void addRole(Role role) {
        ldapTemplate.create(role);
    }

    /*Staffs*/
    public List<Staff> findAllStaffs(String companyId) {
        SearchControls sc = new SearchControls();
        Name dn = LdapNameBuilder.newInstance(companyId)
            .add("ou","staffs")
            .build();
        return ldapTemplate.findAll(dn,sc,Staff.class);
    }

    public List<Staff> findStaffs(String companyId, String departmentId) {
        Name dn = LdapNameBuilder.newInstance(companyId)
            .add("ou","staffs")
            .build();
        return ldapTemplate.find(
                query().base(dn)
                    .where("objectclass").is("inetOrgPerson")
                    .and("ou").is(departmentId),
                Staff.class);
    }

    public Staff findStaff(String staffId) {
        Name dn = LdapNameBuilder.newInstance(staffId)
            .build();
        return ldapTemplate.findByDn(dn, Staff.class);
    }

    public void addStaff(Staff staff) {
        ldapTemplate.create(staff);
    }
}

