package semantic_web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class ContentBasedFiltering {
	public static List<String> genres = new ArrayList<String>();
	public static List<String> title = new ArrayList<String>();
	
	public static List<String> recommendations_trending(String userId) {
		String queryString = "\r\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\r\n"
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"
				+ "SELECT ?show_Id ?titel ?director?cast?genres\r\n"
				+ "WHERE {\r\n"
				+ "  SERVICE rating:sparql\r\n"
				+ "  {\r\n"
				+ "    ?subject <http://www.semanticweb.org/ontologies/2021/10/untitled-ontology-18#title> "+titel+" .\r\n"
				+ "    ?subject <http://www.semanticweb.org/ontologies/2021/10/untitled-ontology-18#cast> "+cast+" .\r\n"
				+ "    ?subject <http://www.semanticweb.org/ontologies/2021/10/untitled-ontology-18#director> "+director+" ?obj .\r\n"
				+ "    ?obj <http://www.semanticweb.org/ontologies/2021/10/untitled-ontology-18#genre> ?genre.\r\n";
	

		recommendations_trending(queryString, serviceEndPoint);

		return recommendations_trending();

	}

	private static List<String> getTrending() {
		String selected_genres = "";
		String trending = "";
		for (String movieId : movieIds) {
			selected_genres = selected_genres + "," + title;
		}

		selected_genres = selected_genres.substring(1);
		for (int i = 0; i < genres.size(); i++) {
			if (i == 0)
				trending = genres.get(i);
			else
				trending = trending + "|" + genres.get(i);
		}
		trending = "\"" + trending + "\"";
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n"
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n"
				+ "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\r\n"
				+ "prefix owl: <http://www.w3.org/2002/07/owl#>\r\n" + "\r\n"
				+ "SELECT ?movieId ?movieTitle ?genres\r\n" + "WHERE {\r\n"
				+ "  ?movies <http://www.semanticweb.org/iti/ontologies/2021/10/untitled-ontology-17#genres> ?genres.\r\n"
				+ "  FILTER regex(?genres," + top3Genres + ",\"i\") .\r\n"
				+ "  ?movies <http://www.semanticweb.org/iti/ontologies/2021/10/untitled-ontology-17#movie_id> ?titles.\r\n"
				+ "  FILTER (?movieId NOT IN (" + selected_genres + "))\r\n"
				+ "  ?movies <http://www.semanticweb.org/iti/ontologies/2021/10/untitled-ontology-17#original_title> ?movieTitle.\r\n"
				+ "}\r\n" + "LIMIT 9";
		return loadTest(query, serviceEndPoint);
	}

	public static void recommendations_trending(String query, String serviceEndPoint) {

		Pattern pattern = Pattern.compile("'[a-zA-Z]*'}");

		QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndPoint, query);

		ResultSet results = qexec.execSelect();
		List<String> movieId = new ArrayList<String>();
		Map<String, Integer> genreFreq = new HashMap<>();
		List<QuerySolution> li = ResultSetFormatter.toList(results);
		for (QuerySolution querySolution : li) {
			movieId.add(querySolution.get("?movieId").asNode().getLiteralValue().toString());
			String str = querySolution.get("?genres").asNode().getLiteralValue().toString();
			Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
				String genre = matcher.group().substring(1, matcher.group().length() - 2);
				int count = genreFreq.getOrDefault(genre, 0);
				genreFreq.put(genre, count + 1);
			}
		}

		Map<String, Integer> sortedMap = genreFreq.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));


		int begin = sortedMap.size() - 3;
		int i = 0;

		List<String> top3Genres = new ArrayList<>();

		for (Entry<String, Integer> genresToCount : sortedMap.entrySet()) {
			if (i++ < begin)
				continue;
			top3Genres.add(genresToCount.getKey());
		}
		genres = trending;
		movieIds = movieId;
	}

	public static List<String> loadTest(String query, String serviceEndPoint) {

		QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndPoint, query);

		ResultSet results = qexec.execSelect();
		List<String> movieId = new ArrayList<String>();
		List<QuerySolution> li = ResultSetFormatter.toList(results);
		for (QuerySolution querySolution : li) {
			movieId.add(querySolution.get("?movieId").asNode().getLiteralValue().toString());
		}
		return movieId;
	}

}
