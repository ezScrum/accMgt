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

	private  String nickname;

	public User(){}

	public Long getId() {
		return id;
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

	public String getNickname(){
    	return nickname;
	}

	public void setNickname(String nickname){
		this.nickname = nickname;
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

