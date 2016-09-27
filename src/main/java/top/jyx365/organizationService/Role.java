package top.jyx365.organizationService;

import java.util.List;
import javax.naming.Name;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;
import org.springframework.ldap.support.LdapNameBuilder;

@Entry(objectClasses = {"organizationalRole"})
public final class Role {
    @Id
    private Name id;

    @Attribute(name="cn")
    private String name;

    @Attribute(name="roleOccupant")
    private List<String> occupants;

    private String description;

    @Attribute(name="ou")
    private String department;



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOccupants(List<String> occupants) {
        this.occupants = occupants;
    }

    public void addOccupant(String occupant) {
        this.occupants.add(occupant);
    }

    public void removeOccupant(String occupant) {
        this.occupants.remove(occupant);
    }

    public List<String> getOccupants() {
        return occupants;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDepartment(String department) {
        this.department = department;
        this.id = LdapNameBuilder.newInstance(department)
            .add("cn",name)
            .build();
    }

    public String getDepartment() {
        return department;
    }

    public void setId(Name id) {
        this.id = id;
    }

    public String getId() {
        return id.toString();
    }
}
