package hibernate.hibernate;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateEnterprise {

	private static SessionFactory sf; // this SessionFactory will be created once and used for all the connections
	private static Productos p;

	HibernateEnterprise() {// constructor
		// sf = HibernateUtil.getSessionFactory();
		sf = new Configuration().configure().buildSessionFactory(); // also works
	}

	public void close() {
		sf.close();
	}

	public void addProduct(String name, double price) {
		Session session = sf.openSession();// session es la variable que tiene el método
											// save para guardar productos

		Transaction tx = null;
		// create the product with the parameters in the method
		Productos p = new Productos();
		p.setNombre(name);
		p.setPrecio(price);
		// keep it in the database=session.save(p)
		try {
			System.out.println("======================================");
			System.out.printf("Insertando la Fila en la Base de Datos: %s, %s\n", name, price);
			System.out.println("======================================");
			tx = session.beginTransaction();
			session.save(p);// we INSERT p into the table PRODUCTS
			tx.commit();// if session.save doesnt produce an exception, we commit; the transaction
		} catch (Exception e) {// if there is any exception, we "rollback" and close safely
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
	}

	public void showProducts() {
		Session session = sf.openSession();
		Transaction tx = null;

		try {
			tx = session.beginTransaction();
			List allproducts = session.createQuery("From Productos").list();

			Iterator it = allproducts.iterator();
			System.out.println("======================================");
			System.out.println("Buscando Productos...");
			System.out.println("======================================");
			while (it.hasNext()) {
				// for (Iterator iterator = allproducts.iterator(); iterator.hasNext();){
				Productos p = (Productos) it.next();
				System.out.println("======================================");
				System.out.println("Id: " + p.getId());
				System.out.println("Nombre: " + p.getNombre());
				System.out.println("Precio: " + p.getPrecio());
				System.out.println("======================================");
			}
			tx.commit();
			System.out.println("======================================");
			System.out.println("Finalizada la Busqueda...");
			System.out.println("======================================");
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public Productos findProductById(int id) {
		Session session = sf.openSession();
		Transaction tx = null;
		Productos p = new Productos();

		try {
			System.out.println("======================================");
			System.out.println("Cargando Producto de la Base de Datos...");
			System.out.println("======================================");
			tx = session.beginTransaction();
			p = (Productos) session.load(Productos.class, id);
			tx.commit();
			System.out.println("======================================");
			System.out.println("Producto con ID -> " + id);
			System.out.println("Su Nombre es -> " + p.getNombre());
			System.out.println("======================================");
		} catch (ObjectNotFoundException e) {
			if (tx != null) {
				System.out.println(e);
				System.out.println("Product not found");
			}
		} catch (Exception e) {
			if (tx != null) {
				System.out.println(e);
				tx.rollback();
			}
		} finally {
			session.close();
		}
		return p;
	}

	public void deleteProductById(int id) {
		Productos p = new Productos();
		Session session = sf.openSession();
		Transaction tx = null;
		try {
			System.out.println("======================================");
			System.out.println("Buscando Producto con ID -> " + id);
			System.out.println("======================================");
			tx = session.beginTransaction();
			p = (Productos) session.get(Productos.class, id);

			if (p != null) {
				System.out.println("======================================");
				System.out.println("Borrando Producto de la Base de Datos...");
				System.out.println("======================================");

				session.delete(p);
				tx.commit();

				System.out.println("======================================");
				System.out.printf(
						"Producto Borrado de la Base de Datos ..." + "\n ID -> %s\n Nombre -> %s\n Precio -> %s",
						p.getId(), p.getNombre(), p.getPrecio());
				System.out.println("\n======================================");
			} else {
				System.out.println("======================================");
				System.out.println("No Se Encontro Ningun Producto con ID -> " + id);
				System.out.println("======================================");
			}
		} catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
	}

	public void updateProductById(int id, String newName, double newPrice) {
		Productos p = new Productos();
		Session session = sf.openSession();
		Transaction tx = null;
		try {
			System.out.println("======================================");
			System.out.println("Modificando el Producto de la Base de Datos...");
			System.out.println("Con los Siguientes Datos...");
			System.out.println("ID -> " + id);
			System.out.println("Nombre -> " + newName);
			System.out.println("Precio -> " + newPrice);
			System.out.println("======================================");
			tx = session.beginTransaction();
			p = (Productos) session.load(Productos.class, id);// we load the product
			System.out.println("======================================");
			System.out.println("Datos del Producto en la Base de Datos...");
			System.out.println("======================================");
			System.out.printf(" ID -> %s\n Nombre -> %s\n Precio -> %s", p.getId(), p.getNombre(), p.getPrecio());
			System.out.println("\n======================================");
			p.setPrecio(newPrice);// we change the properties
			p.setNombre(newName);
			session.update(p);// we update the values in the database
			tx.commit();
			System.out.println("======================================");
			System.out.println("Producto Modificado");
			System.out.println("======================================");
			System.out.printf("Datos del Producto Modificado..." + "\n ID -> %s\n Nombre -> %s\n Precio -> %s",
					p.getId(), p.getNombre(), p.getPrecio());
			System.out.println("\n======================================");
		} catch (Exception e) {
			System.out.println("======================================");
			System.out.println("No Se Encontro el Producto con ID -> " + id);
			System.out.println("======================================");
			if (tx != null) {
				tx.rollback();
			}
		} finally {
			session.close();
		}
	}

	/*
	 ****************************************** HQL 3.3*************************************
	 */

	// Método para buscat todos los productos de la base de datos y mostrarlos
	public void mostrarProductos() {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			// Consultamos todos los productos en la base de datos
			List allProducts = session.createQuery("FROM Productos").list();

			// Iteramos sobre la lista de productos
			Iterator it = allProducts.iterator();
			while (it.hasNext()) {
				// Obtenemos un producto de la lista
				Productos p = (Productos) it.next();

				// Mostramos la información del producto
				System.out.print("Id: " + p.getId());
				System.out.print(" ,Name: " + p.getNombre());
				System.out.println(" ,Price: " + p.getPrecio());
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y hacemos rollback en caso de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para mostrar todos los datos buscando por nombre
	public void mostrarPorNombre(String texto) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;

		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Consultamos todos los productos cuyo nombre contenga el texto proporcionado
			List allProductNames = session.createQuery("FROM Productos p WHERE p.nombre LIKE :texto")
					.setParameter("texto", "%" + texto + "%").list();

			// Iteramos sobre la lista de productos
			Iterator it = allProductNames.iterator();
			while (it.hasNext()) {
				// Obtenemos un producto de la lista
				Productos p = (Productos) it.next();

				// Mostramos la información del producto
				System.out.print("Id: " + p.getId());
				System.out.print(" ,Name: " + p.getNombre());
				System.out.println(" ,Price: " + p.getPrecio());
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y hacemos rollback en caso de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para buscar todos los productos y mostrarlos por ordenados por precio
	public void productosOrdenadosPorPrecio() {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Consultamos todos los productos ordenados por precio de manera ascendente
			List allProducts = session.createQuery("FROM Productos ORDER BY precio ASC").list();

			// Iteramos sobre la lista de productos
			Iterator it = allProducts.iterator();
			while (it.hasNext()) {
				// Obtenemos un producto de la lista
				Productos p = (Productos) it.next();

				// Mostramos la información del producto
				System.out.print("Id: " + p.getId());
				System.out.print(" ,Name: " + p.getNombre());
				System.out.println(" ,Price: " + p.getPrecio());
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y hacemos rollback en caso de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para buscar el precio pasandole el nombre nombre
	public void precioDe(String nombreProducto) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Modificamos la consulta HQL para incluir el parámetro de búsqueda por nombre
			String hql = "FROM Productos p WHERE p.nombre LIKE :nombreProducto";

			// Ejecutamos la consulta con el parámetro
			List<Productos> productos = session.createQuery(hql)
					.setParameter("nombreProducto", "%" + nombreProducto + "%").list();

			// Iteramos sobre la lista de productos
			Iterator<Productos> it = productos.iterator();
			while (it.hasNext()) {
				// Obtenemos un producto de la lista
				Productos p = it.next();

				// Mostramos la información del producto
				System.out.print("Name: " + p.getNombre());
				System.out.println(" ,Price: " + p.getPrecio());
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y hacemos rollback en caso de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para buscar un producto pasandole un id
	public void buscaProducto(int id) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Modificamos la consulta HQL para buscar un producto por ID
			String hql = "FROM Productos p WHERE p.id = :productoId";

			// Ejecutamos la consulta HQL con el parámetro de búsqueda por ID
			Productos producto = (Productos) session.createQuery(hql).setParameter("productoId", id).uniqueResult();

			// Verificamos si se encontró algún producto con la ID proporcionada
			if (producto != null) {
				// Mostramos la información del producto encontrado
				System.out.println("Producto encontrado:");
				System.out.println("ID: " + producto.getId());
				System.out.println("Nombre: " + producto.getNombre());
				System.out.println("Precio: " + producto.getPrecio());

				// Puedes agregar más campos según la estructura de tu clase Productos
			} else {
				// En caso de no encontrar ningún producto con la ID proporcionada
				System.out.println("No se encontró ningún producto con la ID: " + id);
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y hacemos rollback en caso de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	/*
	 ****************************************** HQL 3.1*************************************
	 */

	// Método para mostrar todos los clientes
	public void mostrarClientes() {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			tx = session.beginTransaction();

			// Consultamos todos los clientes en la base de datos
			List allClients = session.createQuery("FROM Clientes").list();

			// Iteramos sobre la lista de clientes
			Iterator it = allClients.iterator();
			while (it.hasNext()) {
				// Obtenemos un producto de la lista
				Clientes c = (Clientes) it.next();

				// Mostramos la información del cliente
				System.out.print("Id: " + c.getId());
				System.out.print(" ,Name: " + c.getNombre());
				System.out.println(" ,Country: " + c.getPais());
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y hacemos rollback en caso de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para agregar un cliente
	public void agregarCliente(String nombre, String pais) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Creamos un nuevo cliente con la información proporcionada
			Clientes nuevoCliente = new Clientes();
			nuevoCliente.setNombre(nombre);
			nuevoCliente.setPais(pais);
			// Puedes agregar más campos según la estructura de tu clase Clientes

			// Guardamos el nuevo cliente en la base de datos
			session.save(nuevoCliente);

			// Confirmamos la transacción
			tx.commit();

			System.out.println("Cliente añadido correctamente:");
			System.out.println("ID: " + nuevoCliente.getId());
			System.out.println("Nombre: " + nuevoCliente.getNombre());
			System.out.println("País: " + nuevoCliente.getPais());
			// Puedes agregar más campos según la estructura de tu clase Clientes
		} catch (HibernateException e) {
			// Manejamos las excepciones y hacemos rollback en caso de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para borrar un Cliente pasandole el Id
	public void borrarClientePorId(int clienteId) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Buscamos el cliente por ID
			Clientes clienteParaBorrar = session.get(Clientes.class, clienteId);

			// Verificamos si el cliente existe
			if (clienteParaBorrar != null) {
				// Borramos el cliente de la base de datos
				session.delete(clienteParaBorrar);

				// Confirmamos la transacción
				tx.commit();

				System.out.println("Cliente con ID " + clienteId + " ha sido borrado exitosamente.");
			} else {
				// Si el cliente no existe, hacemos rollback y mostramos un mensaje
				tx.rollback();
				System.out.println("No se encontró ningún cliente con la ID " + clienteId + ". No ha sido borrado.");
			}
		} catch (HibernateException e) {
			// Manejamos las excepciones y mostramos un mensaje de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para actualizar un cliente pasnadole el Id
	public void actualizarCliente(int id) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		Scanner scanner = new Scanner(System.in);

		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Buscamos el cliente por ID
			Clientes clienteParaActualizar = session.get(Clientes.class, id);

			// Verificamos si el cliente existe
			if (clienteParaActualizar != null) {
				// Mostramos los valores actuales del cliente
				System.out.println("Valores actuales del cliente con ID " + id + ":");
				System.out.println("Nombre: " + clienteParaActualizar.getNombre());
				System.out.println("País: " + clienteParaActualizar.getPais());

				// Preguntamos si se desea actualizar el nombre
				System.out.print("¿Quieres un nuevo nombre? (s/n): ");
				String respuestaNombre = scanner.nextLine().trim().toLowerCase();

				// Si la respuesta es afirmativa, solicitamos el nuevo nombre
				if (respuestaNombre.equals("s")) {
					System.out.print("Introduce el nuevo nombre: ");
					String nuevoNombre = scanner.nextLine().trim();
					clienteParaActualizar.setNombre(nuevoNombre);
				}

				// Preguntamos si se desea actualizar el país
				System.out.print("¿Quieres un nuevo país? (s/n): ");
				String respuestaPais = scanner.nextLine().trim().toLowerCase();

				// Si la respuesta es afirmativa, solicitamos el nuevo país
				if (respuestaPais.equals("s")) {
					System.out.print("Introduce el nuevo país: ");
					String nuevoPais = scanner.nextLine().trim();
					clienteParaActualizar.setPais(nuevoPais);
				}

				// Confirmamos la transacción
				tx.commit();

				System.out.println("Cliente actualizado exitosamente.");
			} else {
				// Si el cliente no existe, mostramos un mensaje
				System.out.println(
						"No se encontró ningún cliente con la ID " + id + ". No se ha actualizado ningún cliente.");
			}
		} catch (HibernateException e) {
			// Manejamos las excepciones y mostramos un mensaje de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
			scanner.close();
		}
	}

	// Método para borrar el cliente pasandole un nombre
	public void borrarClientePorNombre(String nombre) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Consultamos todos los clientes con el nombre proporcionado
			List<Clientes> clientesParaBorrar = session.createQuery("FROM Clientes c WHERE c.nombre = :nombre")
					.setParameter("nombre", nombre).list();

			// Verificamos si se encontraron clientes con el nombre proporcionado
			if (!clientesParaBorrar.isEmpty()) {
				// Borramos cada cliente de la lista
				for (Clientes cliente : clientesParaBorrar) {
					session.delete(cliente);
				}

				// Confirmamos la transacción
				tx.commit();

				System.out.println("Clientes con el nombre '" + nombre + "' han sido borrados exitosamente.");
			} else {
				// Si no se encontraron clientes con el nombre proporcionado, mostramos un
				// mensaje
				System.out.println(
						"No se encontraron clientes con el nombre '" + nombre + "'. No se ha borrado ningún cliente.");
			}
		} catch (HibernateException e) {
			// Manejamos las excepciones y mostramos un mensaje de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para mostrar cuentos clientes tenemos en ese país y sus datos
	public void mostrarPorPais(String pais) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Consultamos el número de clientes en el país proporcionado
			Long cantidadClientes = (Long) session.createQuery("SELECT COUNT(c) FROM Clientes c WHERE c.pais = :pais")
					.setParameter("pais", pais).uniqueResult();

			// Mostramos el número de clientes en el país
			System.out.println("Número de clientes en " + pais + ": " + cantidadClientes);

			// Consultamos y mostramos los datos de cada cliente en el país
			List<Clientes> clientesEnPais = session.createQuery("FROM Clientes c WHERE c.pais = :pais")
					.setParameter("pais", pais).list();

			// Iteramos sobre la lista de clientes
			for (Clientes cliente : clientesEnPais) {
				System.out.println("ID: " + cliente.getId());
				System.out.println("Nombre: " + cliente.getNombre());
				System.out.println("País: " + cliente.getPais());
				// Puedes agregar más campos según la estructura de tu clase Clientes
				System.out.println("------------------------");
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y mostramos un mensaje de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}

	// Método para muscar el país de un nombre en concreto
	public void buscarPaisDe(String nombre) {
		// Abrimos una nueva sesión de Hibernate
		Session session = sf.openSession();

		// Iniciamos una transacción para las operaciones en la base de datos
		Transaction tx = null;
		try {
			// Iniciamos la transacción
			tx = session.beginTransaction();

			// Consultamos el país del cliente con el nombre proporcionado
			String paisCliente = (String) session.createQuery("SELECT c.pais FROM Clientes c WHERE c.nombre = :nombre")
					.setParameter("nombre", nombre).uniqueResult();

			// Verificamos si se encontró un cliente con el nombre proporcionado
			if (paisCliente != null) {
				// Mostramos el país del cliente
				System.out.println("País del cliente '" + nombre + "': " + paisCliente);
			} else {
				// Si no se encontró el cliente, mostramos un mensaje
				System.out.println("No se encontró ningún cliente con el nombre '" + nombre + "'.");
			}

			// Confirmamos la transacción
			tx.commit();
		} catch (HibernateException e) {
			// Manejamos las excepciones y mostramos un mensaje de error
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			// Cerramos la sesión al finalizar
			session.close();
		}
	}
}