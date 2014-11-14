package de.mpg.imeji.presentation.user;

import java.util.Collection;
import java.util.List;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.presentation.collection.CreateCollectionBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;
import de.mpg.imeji.presentation.util.ImejiFactory;

/**
 * The JSF Composite for a {@link Person}
 * 
 * @author saquet
 *
 */
@ManagedBean(name = "PersonBean")
@ViewScoped
public class PersonBean {
	private SessionBean sb;

	private String personURI;

	public PersonBean() {
		sb = (SessionBean) BeanHelper.getSessionBean(SessionBean.class);
	}

	/**
	 * Get all {@link Person} in imeji and return a String which can be read for
	 * the JQUERY autocomplete
	 * 
	 * @return
	 */
	public String getAllPersonsAsJQuery() {
		UserController uc = new UserController(sb.getUser());
		String s = "";
		Collection<Person> persons = uc.searchPersonByName("");
		for (Person p : persons) {
			if (!"".equals(s))
				s += ",";
			s += "{";
			s += "label: \"" + p.getCompleteName() + "("
					+ p.getOrganizationString() + ")\",";
			s += "value : \"";
			s += p.getId();
			s += "\"}";
		}
		return s;
	}

	public String getAllOrganizationsAsString() {
		UserController uc = new UserController(sb.getUser());
		String s = "";
		Collection<Organization> orgs = uc.searchOrganizationByName("");
		for (Organization o : orgs) {
			if (!"".equals(s))
				s += ",";
			s += "{";
			s += "label: \"" + o.getName() + "\",";
			s += "value : \"";
			s += o.getId() + ",";
			s += o.getName() + ",";
			s += o.getDescription() + ",";
			s += o.getIdentifier() + ",";
			s += o.getCity() + ",";
			s += o.getCountry() + ",";
			s += "\"}";
		}
		return s;
	}

	/**
	 * Change the person
	 * 
	 * @return
	 */
	public String changePerson(Object bean, int position) {
		Person person = loadPerson(personURI);
		if (bean instanceof CreateCollectionBean) {
			List<Person> l = (List<Person>) ((CreateCollectionBean) bean)
					.getCollection().getMetadata().getPersons();
			l.set(position, person.clone());
		} else if (bean instanceof UserCreationBean) {
			((UserCreationBean) bean).getUser().setPerson(person.clone());
		}
		return ":";
	}

	/**
	 * Load the {@link Person} with the passed uri
	 * 
	 * @param uri
	 * @return
	 */
	private Person loadPerson(String uri) {
		if (uri != null) {
			try {
				UserController uc = new UserController(sb.getUser());
				return uc.retrievePersonById(personURI);
			} catch (Exception e) {
				BeanHelper.error(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Get the {@link Person} which comes from the parent bean
	 */
	private Person getPersonFromParentBean() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExpression = facesContext
				.getApplication()
				.getExpressionFactory()
				.createValueExpression(elContext, "#{cc.attrs.person}",
						Person.class);

		return (Person) valueExpression.getValue(elContext);
	}

	/**
	 * Add an organization to an author of the {@link CollectionImeji}
	 * 
	 * @param authorPosition
	 * @param organizationPosition
	 * @return
	 */
	public String addOrganization(int organizationPosition) {
		List<Organization> orgs = (List<Organization>) getPersonFromParentBean()
				.getOrganizations();
		Organization o = ImejiFactory.newOrganization();
		o.setPos(organizationPosition);
		orgs.add(organizationPosition, o);
		return "";
	}

	/**
	 * Remove an organization to an author of the {@link CollectionImeji}
	 * 
	 * @return
	 */
	public String removeOrganization(int organizationPosition) {

		List<Organization> orgs = (List<Organization>) getPersonFromParentBean()
				.getOrganizations();
		if (orgs.size() > 1)
			orgs.remove(organizationPosition);
		else
			BeanHelper.error(((SessionBean) BeanHelper
					.getSessionBean(SessionBean.class))
					.getMessage("error_author_need_one_organization"));
		return "";
	}

	/**
	 * Listener
	 * 
	 * @param event
	 */
	public void personListener(ValueChangeEvent event) {
		this.personURI = event.getNewValue().toString();
	}

	/**
	 * Getter
	 * 
	 * @return
	 */
	public String getPersonURI() {
		return personURI;
	}

	/**
	 * setter
	 * 
	 * @param personURI
	 */
	public void setPersonURI(String personURI) {
		this.personURI = personURI;
	}

}