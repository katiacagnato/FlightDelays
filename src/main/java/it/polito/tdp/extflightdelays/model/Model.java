package it.polito.tdp.extflightdelays.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private ExtFlightDelaysDAO dao= new ExtFlightDelaysDAO();
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private Map<Integer, Airport> mappa;
	Collection<Airport> lista ;
	List<Rotta> listar;
	String output;
	
	public Model() {
		dao= new ExtFlightDelaysDAO();
		mappa=dao.loadAllAirports();
	}
	
	public void CreaGrafo(int minimoCompagnie){
		listar= new LinkedList<>();
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		lista = dao.AirportXCompany(minimoCompagnie);
		Graphs.addAllVertices(grafo, lista);
		System.out.println(grafo.vertexSet().size()+ " numero input : "+ minimoCompagnie);
		
		
		for(Rotta r : dao.Archi()) {
						//Se i vertici non sono tutti gli elementi del db ma variano in base ad un input
						// metto la condizione che gli archi leghino solo i vertici del grafo
			if(this.grafo.containsVertex(r.getA1()) &&  											
						this.grafo.containsVertex(r.getA2())) {
				DefaultWeightedEdge e = this.grafo.getEdge(r.getA1(), r.getA2());
				if(e == null) {
					Graphs.addEdgeWithVertices(grafo, r.getA1(), r.getA2(), r.getPeso());
				} else{
					double pesoVecchio = this.grafo.getEdgeWeight(e);
					double pesoNuovo = pesoVecchio + r.getPeso();
					this.grafo.setEdgeWeight(e, pesoNuovo);
				}
			}
		
		}
		
	}

	public Set<Airport> getVertici() {
		if(grafo != null)
			return grafo.vertexSet();
		
		return null;
	}
	
	public int getNVertici() {
		if(grafo != null)
			return grafo.vertexSet().size();
		
		return 0;
	}
	
	public int getNArchi() {
		if(grafo != null)
			return grafo.edgeSet().size();
		
		return 0;
	}
	
	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
	
	
	
}
