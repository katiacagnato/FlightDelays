package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;
import it.polito.tdp.extflightdelays.model.Rotta;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	Map<Integer, Airport> mappa= new HashMap<>();
	public Map<Integer, Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				//result.add(airport);
				mappa.put(rs.getInt("ID"), airport);
			}

			conn.close();
			return mappa;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	Map<Integer, Airport> mappaXC = new HashMap<>();
	public Collection<Airport> AirportXCompany(int numeroCompagnie) {
		//MIA
		
		String sql1 = "SELECT  f.ORIGIN_AIRPORT_ID "
				+ "FROM airports a, flights f, airlines al "
				+ "WHERE a.ID=f.ORIGIN_AIRPORT_ID OR a.id = f.DESTINATION_AIRPORT_ID AND al.ID=f.AIRLINE_ID "
				+ "GROUP BY f.ORIGIN_AIRPORT_ID "
				+ "HAVING COUNT(distinct(f.AIRLINE_ID))>= ? ";
		
		String sql2= "SELECT a.id "
				+ "	FROM airports a, flights f "
				+ "	WHERE (a.id = f.ORIGIN_AIRPORT_ID OR a.id = f.DESTINATION_AIRPORT_ID) "
				+ "	GROUP BY a.id "
				+ "	HAVING COUNT(DISTINCT(f.AIRLINE_ID)) >= ? ";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql2);
			st.setInt(1, numeroCompagnie);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				int id= rs.getInt("a.id");
				mappa= this.loadAllAirports();
				Airport a = mappa.get(id);
				mappaXC.put(id, a);
				
			}

			conn.close();
			return mappaXC.values();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	
	public List<Rotta> Archi() {
		//MIA
		String sql = "SELECT a1.ID, a2.ID, COUNT(f.ID) AS Peso "
				+ "FROM airports a1, airports a2, flights f "
				+ "WHERE a1.ID=f.ORIGIN_AIRPORT_ID AND a2.ID=f.DESTINATION_AIRPORT_ID "
				+ "AND a1.ID > a2.ID "
				+ "GROUP BY a1.ID, a2.ID";
		
		String sql1 = "SELECT f.ORIGIN_AIRPORT_ID , f.DESTINATION_AIRPORT_ID , COUNT(*) AS Peso "
				+ "FROM Flights f "
				+ "GROUP BY f.ORIGIN_AIRPORT_ID, f.DESTINATION_AIRPORT_ID";
		
		List<Rotta> rotte = new ArrayList<>();
		//System.out.println(mappaXC);
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql1);
			
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
//				int idP= rs.getInt("a1.ID");
//				int idA= rs.getInt("a2.ID");
				int idP= rs.getInt("f.ORIGIN_AIRPORT_ID");
				int idA= rs.getInt("f.DESTINATION_AIRPORT_ID");
				int peso = rs.getInt("Peso");
				//if(mappaXC.containsKey(idP) || mappaXC.containsKey(idA)) {
				Airport a1 = mappa.get(idP);
				Airport a2 = mappa.get(idA);
				Rotta r= new Rotta(a1, a2, peso);
				rotte.add(r);
			//	}
			//	else 
				//	return null;
			}

			conn.close();
			return rotte;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}
