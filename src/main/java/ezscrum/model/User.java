package ezscrum.model;

import org.hibernate.validator.constraints.NotBlank;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String username;

    private String email;

    private String password;

	private boolean enabled;

	private boolean systemrole;

	public User(){}

	public User (String username, String password){
		this.username = username;
		this.password = password;
	}

	public User (String username){
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(unique = true)

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

    public String getPassword(){
        return password;
    }

    public boolean getSystemRole(){
		return systemrole;
	}

	public void setSystemRole(boolean systemrole){
		this.systemrole = systemrole;
	}

    public void setPassword(String password){
		this.password = password;
	}

	public boolean isEnabled() {
    	return enabled;
	}

	public void setEnabled(boolean enabled) {
    	this.enabled = enabled;
	}
}

