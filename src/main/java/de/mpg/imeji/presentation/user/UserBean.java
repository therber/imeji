/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import com.hp.hpl.jena.sparql.pfunction.library.container;

import de.mpg.imeji.logic.Imeji;
import de.mpg.imeji.logic.ImejiSPARQL;
import de.mpg.imeji.logic.auth.util.AuthUtil;
import de.mpg.imeji.logic.controller.GrantController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.controller.UserController.USER_TYPE;
import de.mpg.imeji.logic.search.query.SPARQLQueries;
import de.mpg.imeji.logic.util.StringHelper;
import de.mpg.imeji.logic.vo.Container;
import de.mpg.imeji.logic.vo.Grant;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Grant.GrantType;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.beans.PropertyBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;
import de.mpg.imeji.presentation.util.ObjectLoader;

public class UserBean {
	private User user;
	private String newEmail = null;
	private String newFamilyName = null;
	private String newPassword = null;
	private String repeatedPassword = null;
	private SessionBean session = (SessionBean) BeanHelper
			.getSessionBean(SessionBean.class);
	private String id;
	private List<SharedHistory> roles = new ArrayList<SharedHistory>();
	private boolean changeEmail = false;
	private boolean changeFamilyName = false;
	private boolean edit = false;

	public UserBean() {
	}

	public UserBean(String email) {
		init(email);
	}

	/**
	 * Method called from the htmal page
	 * 
	 * @return
	 */
	public String getInit() {
		init(FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("id"));
		return "";
	}

	private void init(String id) {
		try {
			this.id = id;
			newPassword = null;
			repeatedPassword = null;
			retrieveUser();
			if (user != null) {
				this.roles = AuthUtil.getAllRoles(user, session.getUser());
				this.newEmail = user.getEmail();
				this.newFamilyName = user.getPerson().getFamilyName();
				this.changeEmail = false;
				this.changeFamilyName = false;
				this.setEdit(false);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retrieve the current user
	 */
	public void retrieveUser() {
		if (id != null && session.getUser() != null) {
			user = ObjectLoader.loadUser(id, session.getUser());
		} else if (id != null && session.getUser() == null) {
			LoginBean loginBean = (LoginBean) BeanHelper
					.getRequestBean(LoginBean.class);
			loginBean.setLogin(id);
		}
	}

	/**
	 * Change the password of the user
	 * 
	 * @throws Exception
	 */
	public void changePassword() throws Exception {
		if (user != null && newPassword != null && !"".equals(newPassword)) {
			if (newPassword.equals(repeatedPassword)) {
				user.setEncryptedPassword(StringHelper
						.convertToMD5(newPassword));
				updateUser();
				BeanHelper.info(session
						.getMessage("success_change_user_password"));
			} else {
				BeanHelper.error(session
						.getMessage("error_user_repeat_password"));
			}
			reloadPage();
		}
	}

	public void toggleEdit() {
		this.edit = edit ? false : true;
	}

	/**
	 * Add a new empty organization
	 * 
	 * @param index
	 */
	public void addOrganization(int index) {
		((List<Organization>) this.user.getPerson().getOrganizations()).add(
				index, ImejiFactory.newOrganization());
	}

	/**
	 * Remove an nth organization
	 * 
	 * @param index
	 */
	public void removeOrganization(int index) {
		List<Organization> orgas = (List<Organization>) this.user.getPerson()
				.getOrganizations();
		if (orgas.size() > 1)
			orgas.remove(index);
	}

	/**
	 * Unshare the {@link Container} for one {@link User} (i.e, remove all
	 * {@link Grant} of this {@link User} related to the {@link container})
	 * 
	 * @param sh
	 */
	public void revokeGrants(SharedHistory sh) {
		sh.getSharedType().clear();
		sh.update();
	}

	/**
	 * Toggle the Admin Role of the {@link User}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void toggleAdmin() throws Exception {
		GrantController gc = new GrantController();
		if (user.isAdmin()) {
			Grant g = AuthUtil.extractGrant((List<Grant>) user.getGrants(),
					PropertyBean.baseURI(), null, GrantType.ADMIN);
			gc.removeGrants(user, (List<Grant>) gc.toList(g), session.getUser());
		} else {
			Grant g = new Grant(GrantType.ADMIN, URI.create(PropertyBean
					.baseURI()));
			gc.addGrants(user, (List<Grant>) gc.toList(g), session.getUser());
		}
	}

	public boolean isUniqueAdmin() {
		return ImejiSPARQL.exec(SPARQLQueries.selectUserSysAdmin(),
				Imeji.userModel).size() == 1;
	}

	/**
	 * Toggle the create collction role of the {@link User}
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void toggleCreateCollection() throws Exception {
		GrantController gc = new GrantController();
		if (user.isAllowedToCreateCollection()) {
			Grant g = AuthUtil.extractGrant((List<Grant>) user.getGrants(),
					PropertyBean.baseURI(), null, GrantType.CREATE);
			gc.removeGrants(user, (List<Grant>) gc.toList(g), session.getUser());
		} else {
			Grant g = new Grant(GrantType.CREATE, URI.create(PropertyBean
					.baseURI()));
			gc.addGrants(user, (List<Grant>) gc.toList(g), session.getUser());
		}
	}

	private boolean inputValid() {
		boolean valid = true;
		if (changeFamilyName) {
			if (newFamilyName == null || "".equals(newFamilyName)) {
				BeanHelper
						.error(session.getMessage("error_user_name_unfilled"));
				valid = false;
			} else if (changeEmail && changeEmail()) {
				this.user.getPerson().setFamilyName(newFamilyName);
			} else
				this.user.getPerson().setFamilyName(newFamilyName);
		}
		if (changeEmail && !changeEmail()) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Update the user in jena
	 */
	public void updateUser() {
		if (user != null && inputValid()) {
			UserController controller = new UserController(session.getUser());
			try {
				controller.update(user, session.getUser());
			} catch (Exception e) {
				BeanHelper.error(e.getMessage());
				e.printStackTrace();
			}
			// finally
			// {
			// reloadPage();
			// }
		}
	}

	public void changeEmailListener() {
		this.changeEmail = true;
	}

	public void changeFamilyNameListerner() {
		this.changeFamilyName = true;
	}

	/**
	 * Change the Email of the user:<br/>
	 * if the new email is valid and is not already used, then create a new user
	 * with this email and delete the old one
	 */
	public boolean changeEmail() {
		boolean emailChanged = true;
		User newUser = user.clone(newEmail);
		if (!UserCreationBean.isValidEmail(newUser.getEmail())) {
			BeanHelper.error(session.getMessage("error_user_email_not_valid"));
			emailChanged = false;
		} else {
			try {
				if (UserCreationBean.userAlreadyExists(newUser)) {
					BeanHelper.error(session
							.getMessage("error_user_already_exists"));
					emailChanged = false;
				} else {
					UserController uc = new UserController(session.getUser());
					// Create the new user
					uc.create(newUser, USER_TYPE.RESTRICTED);
					// If the edited user is the current user, put the new user
					// in the session
					if (session.getUser().getEmail().equals(user.getEmail())) {
						session.setUser(newUser);
						uc = new UserController(session.getUser());
					}
					// delete the old user
					uc.delete(user);
					init(newUser.getEmail());
				}
			} catch (Exception e) {
				BeanHelper.error(session.getMessage("error") + ": " + e);
				emailChanged = false;
			} finally {
				reloadPage();
			}
		}
		return emailChanged;
	}

	/**
	 * Reload the page with the current user
	 * 
	 * @throws IOException
	 */
	private void reloadPage() {
		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(getUserPageUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * return the URL of the current user
	 * 
	 * @return
	 */
	public String getUserPageUrl() {
		Navigation navigation = (Navigation) BeanHelper
				.getApplicationBean(Navigation.class);
		return navigation.getUserUrl() + "?id=" + user.getEmail();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getRepeatedPassword() {
		return repeatedPassword;
	}

	public void setRepeatedPassword(String repeatedPassword) {
		this.repeatedPassword = repeatedPassword;
	}

	/**
	 * @return the newEmail
	 */
	public String getNewEmail() {
		return newEmail;
	}

	/**
	 * @param newEmail
	 *            the newEmail to set
	 */
	public void setNewEmail(String newEmail) {
		this.newEmail = newEmail;
	}

	/**
	 * @return the roles
	 */
	public List<SharedHistory> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(List<SharedHistory> roles) {
		this.roles = roles;
	}

	/**
	 * @return the edit
	 */
	public boolean isEdit() {
		return edit;
	}

	/**
	 * @param edit
	 *            the edit to set
	 */
	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public String getNewFamilyName() {
		return newFamilyName;
	}

	public void setNewFamilyName(String newFamilyName) {
		this.newFamilyName = newFamilyName;
	}
}
