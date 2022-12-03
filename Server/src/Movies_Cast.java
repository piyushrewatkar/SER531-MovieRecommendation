package semantic_web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.jena.query.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class Services {

    public List<Movie> getMovieRecommendations(List<Integer> showid) throws Exception {
        if(showid.size()==0)
        return null;
		List<Movie> result=new ArrayList<>();
		for(Integer iter:showid){
			result.add(getMovieDetails(String.valueOf(iter)));
		}
		System.out.print(result);
        return result;
    }

    public List<Integer> getMovieRecommendations(String castid){
    	String query = "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
		+"\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
		+"\nPREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
		+"\nPREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
		+"\nPREFIX use: <http://www.semanticweb.org/ontologies/2021/10/untitled-ontology-53#>"
		+"\nSelect DISTINCT ?title"
		+"\n	WHERE{"
		+"\n	  ?cast rdf:type use:cast."
		+"\n	  ?cast use:castid ?castid2."
		+"\n	  ?cast use:genre ?avg_user_rating."
		+"\n	  ?avg_user_rating use:showid ?title."
		+"\n	  ?avg_user_rating use:rating ?rating"
		+"\n	  {Select ?castid ?showid ?title"
			+"\n		  WHERE {"
				+"\n	  ?cast rdf:type use:cast."
				+"\n  ?cast use:castid ?castid."
				+"\n  ?cast use:genre ?avg_user_rating."
				+"\n  ?avg_user_rating use:showid ?showid2."
				+"\n  ?avg_user_rating use:rating ?rating"
				+"\n  {SELECT ?showid ?showid ?titel"
					+"\n	WHERE {"
						+"\n	?cast rdf:type use:cast."
						+"\n	?cast use:castid ?castid3."
						+"\n	?cast use:genre ?avg_user_rating."
						+"\n	?avg_user_rating use:showid ?showid."
						+"\n	?avg_user_rating use:rating ?rating"
						+"\n	FILTER(?castid3 = "+castid+" && ?rating > 3.5 )."
						+"\n	}"
						+"\n	LIMIT 1"
						+"\n	  }"
						+"\n	FILTER (?showid2 IN (?showid) && ?castid !="+castid+" && ?rating > 3.5)."
						+"\n  }"
						+"\n  LIMIT 1"
						+"\n  }"
		  
						+"\nFILTER (?castid2 IN (?castid)  && ?showid3 NOT IN (?showid) && ?rating > 3.5)."
		  
						+"\n}"
						+"\nLIMIT 9";
       return getMovieRecommendations(query);
       	 
    }

	public List<Integer> getMovieRecommendations_genre(String query){
		QueryExecution qexec = QueryExecutionFactory.sparqlService(temp_serviceEndPoint,query);	
		ResultSet results = qexec.execSelect();
		List<Integer> lstofmovie = new ArrayList<>();

        List<QuerySolution> solutions = ResultSetFormatter.toList(results);
		for(QuerySolution sol : solutions) {
			lstofmovie.add((sol.getLiteral("?showid3").getInt()));	
		}
		return lstofmovie;
}

    public Movie getShowInfo(String showid) throws Exception {
		System.out.println(showid);
        String queryString = "\n PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                +"\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                +"\n PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                +"\n PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                +"\n PREFIX ds1: <http://www.semanticweb.org/ontologies/2021/10/untitled-ontology-52#>"
                +"\n PREFIX ds2: <http://www.semanticweb.org/iti/ontologies/2021/10/untitled-ontology-17#>"
                +"\n SELECT ?movie_id ?cast_name ?cast_character ?original_title ?overview ?release_date ?genre"
				+"\n ?vote_average ?original_language"
                +"\n WHERE {"    
                +"\n SERVICE movie:sparql {"
                +"\n ?movies rdf:type ds2:Movies."
                +"\n ?movies ds2:movie_id ?movie_id."
                +"\n ?movies ds2:original_title ?original_title."
                +"\n ?movies ds2:genres ?genre."
                +"\n ?movies ds2:overview ?overview."
                +"\n ?movies ds2:vote_average ?vote_average."
                +"\n ?movies ds2:release_date ?release_date."
                +"\n ?movies ds2:original_language ?original_language."
                + "\n FILTER(?movie_id = "+showid+")."
                    +"\n }"            
                    +"\n SERVICE cast:sparql {"
                        +"\n ?movie ds1:showid ?movie_id."
                        +"\n ?movie ds1:has_cast ?cast."
                        +"\n ?cast ds1:cast_name ?cast_name."
                        +"\n ?cast ds1:cast_character ?cast_character."
                        + "\n FILTER(?movie_id = "+showid+")."
                    +"\n }"
                    
                +"\n }";


        QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndPoint, queryString);
        ResultSet results = qexec.execSelect();

		Movie movie = new Movie();
		List<Cast> casts = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
        List<QuerySolution> solutions = ResultSetFormatter.toList(results);
		boolean dataUnset = true;
        for(QuerySolution sol : solutions) {
			if (dataUnset) {
				movie.setMovie_id(sol.getLiteral("movie_id").getInt());

				String genre = sol.getLiteral("genre").toString();
				genre = genre.replace('\'', '\"');
				movie.setGenre(mapper.readValue(genre, new TypeReference<List<Genre>>(){}));
				movie.setOriginal_title(sol.getLiteral("original_title").toString());
				movie.setOverview(sol.getLiteral("overview").toString());
				movie.setRelease_date(sol.getLiteral("release_date").getInt());
				movie.setVote_average(sol.getLiteral("vote_average").getDouble());
				movie.setOriginal_language(sol.getLiteral("original_language").toString());
				Date date = new SimpleDateFormat("D").parse(sol.getLiteral("release_date").toString());
				movie.setRelease(date);
			}

			Cast cast = new Cast(sol.getLiteral("cast_name").toString(),
					          sol.getLiteral("cast_character").toString());

			casts.add(cast);

			dataUnset = false;
        }

		movie.setCast(casts);
        return movie;
    }

    public List<Movie> getTrendingMovies()  throws Exception
    {
        String queryString = "\n PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                +"\n PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                +"\n PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                +"\n PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                +"\n PREFIX movie: <http://www.semanticweb.org/iti/ontologies/2021/10/untitled-ontology-17#>"
                +"\n PREFIX mo: <http://purl.org/ontology/mo/>"
                +"\n PREFIX ds1: <http://ec2-18-205-117-22.compute-1.amazonaws.com:3030/movies/>"
                +"\n SELECT ?title ?id ?overview ?release_date ?language ?popularity ?vote_average ?genre"
                +"\n WHERE {"
                +"\n SERVICE ds1:sparql {"
                +"\n ?movie rdf:type movie:Movies."
                +"\n ?movie movie:movie_id ?id."
                +"\n ?movie movie:original_title ?title."
                +"\n ?movie movie:overview ?overview."
                +"\n ?movie movie:original_language ?language."
				+"\n ?movie movie:vote_average ?vote_average."
                +"\n ?movie movie:release_date ?release_date."
				+"\n ?movie movie:genres ?genre."
                +"\n ?movie movie:popularity ?popularity."
                    +"\n }"
                    +"\n }"
                        +"\n ORDER BY DESC (?release_date) (?popularity)"
                        + "\n LIMIT 9";

        QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndPoint, queryString);

        ResultSet results = qexec.execSelect();
		List<Movie> lstofmovie = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

        List<QuerySolution> solutions = ResultSetFormatter.toList(results);
		for(QuerySolution sol : solutions) {
				Movie movie = new Movie();
				movie.setMovie_id(sol.getLiteral("id").getInt());

				String genre = sol.getLiteral("genre").toString();
				genre = genre.replace('\'', '\"');
				movie.setGenre(mapper.readValue(genre, new TypeReference<List<Genre>>(){}));
				movie.setOriginal_title(sol.getLiteral("title").toString());
				movie.setOverview(sol.getLiteral("overview").toString());
				movie.setRelease_date(sol.getLiteral("release_date").getInt());
				movie.setOriginal_language(sol.getLiteral("language").toString());
				movie.setVote_average(sol.getLiteral("vote_average").getDouble());
				Date date = new SimpleDateFormat("D").parse(sol.getLiteral("release_date").toString());
				movie.setRelease(date);
				System.out.println(movie);
				lstofmovie.add(movie);
		}
        return lstofmovie;
    }
    
    public List<Movie> getRecommendation_cast(String castId) throws Exception
    {
    	List<String> listOfshowids = ContentBasedFiltering.contentBasedFiltering(castId);
    	
    	List<Movie> movieDetails = new ArrayList<Movie>();
    	if(listOfshowids == null)
    		return null;
    	
    	for(String showids: listOfshowids)
    	{
    		movieDetails.add(getMovieDetails(showids));
    	}
    	return movieDetails;
    }
}
