package se.citerus.dddsample.repository;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CargoRepository;
import se.citerus.dddsample.domain.TrackingId;

public class CargoRepositoryHibernate implements CargoRepository {
	private HibernateTemplate hibernateTemplate;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	} 
	
	public Cargo find(TrackingId trackingId) {
		return (Cargo) hibernateTemplate.load(Cargo.class, trackingId);
	}
}
