package Morphophonemic;

import java.util.HashMap;

/**
 * enum of languages
 * at the moment only two languages exist: German and IPA_German 
 * @author abischoff
 *
 */
public enum MorphemeLanguage implements Category {
	
	Deutsch { 	
		/**
		 * verbal endings
		 */
		public HashMap<String,String> verb = new HashMap<String,String>(){

		private static final long serialVersionUID = 1L;

	{ 
		  put( "PrÃ¤sens_ich", "e$" ); 
		  put( "PrÃ¤sens_du", "((est$)|(st$)|(Ãt$))" ); 
		  put( "PrÃ¤sens_er, sie, es", "((et$)|(t$))" ); 
		  put( "PrÃ¤sens_wir", "en$");
		  put("PrÃ¤sens_ihr", "((et$)|(t$))");
		  put("PrÃ¤sens_sie", "en$");
		  put ("Konjunktiv_PrÃ¤sens_du", "((est$)|(st$)|(Ãt$))");
		  put ("Konjunktiv_PrÃ¤sens_er, sie, es", "e$");
		  put ("Konjunktiv_PrÃ¤sens_ihr", "((et$)|(t$))");
		  put("Partizip I", "end$");
		  put( "Partizip II", "((^ge)|(et$)|(t$)|(en$))" ); 
		  put( "PrÃ¤teritum_ich", "((ete$)|(te$))" ); 
		  put( "PrÃ¤teritum_du", "((etest$)|(test$))" ); 
		  put( "PrÃ¤teritum_er, sie, es", "((ete$)|(te$))" ); 
		  put( "PrÃ¤teritum_wir", "((eten$)|(ten$))" ); 
		  put( "PrÃ¤teritum_ihr", "((etet$)|(tet$))" ); 
		  put( "PrÃ¤teritum_sie", "((eten$)|(ten$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_ich", "((ete$)|(te$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_du", "((etest$)|(test$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_er, sie, es", "((ete$)|(te$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_wir", "((eten$)|(ten$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_ihr", "((etet$)|(tet$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_sie", "((eten$)|(ten$))" ); 
		  put( "Konjunktiv II_ich", "((ete$)|(te$)|(e$))" ); 
		  put( "Imperativ Singular", "e$" ); 
		  put( "Imperativ Plural", "((t$)|(et$))" );

		  put( "PrÃ¤sens_ich*", "e$" ); 
		  put( "PrÃ¤sens_du*", "((est$)|(st$)|(Ãt$))" ); 
		  put( "PrÃ¤sens_er, sie, es*", "((et$)|(t$))" );
		  put ("PrÃ¤sens_wir*", "en$");
		  put("PrÃ¤sens_ihr*", "((et$)|(t$))");
		  put("PrÃ¤sens_sie*", "en$");
		  put ("Konjunktiv_PrÃ¤sens_du*", "((est$)|(st$)|(Ãt$))");
		  put("Konjunktiv_PrÃ¤sens_er, sie, es*", "e$");
		  put ("Konjunktiv_PrÃ¤sens_ihr*", "((et$)|(t$))");
		  put( "Partizip II*", "((^ge)|(t$)|(en$))" ); 
		  put( "PrÃ¤teritum_ich*", "((ete$)|(te$))" ); 
		  put( "PrÃ¤teritum_du*", "((etest$)|(test$))" ); 
		  put( "PrÃ¤teritum_er, sie, es*", "((ete$)|(te$))" ); 
		  put( "PrÃ¤teritum_wir*", "((eten$)|(ten$))" ); 
		  put( "PrÃ¤teritum_ihr*", "((etet$)|(tet$))" ); 
		  put( "PrÃ¤teritum_sie*", "((eten$)|(ten$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_ich*", "((ete$)|(te$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_du*", "((etest$)|(test$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_er, sie, es*", "((ete$)|(te$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_wir*", "((eten$)|(ten$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_ihr*", "((etet$)|(tet$))" ); 
		  put( "Konjunktiv_PrÃ¤teritum_sie*", "((eten$)|(ten$))" ); 
		  put( "Konjunktiv II_ich", "((ete$)|(te$)|(e$))" ); 
		  put( "Imperativ Singular*", "e$" ); 
		  put( "Imperativ Singular**", "e$" );
		  put( "Imperativ Plural*", "((t$)|(et$))" );
		  
		  put( "Nominativ Singular", "");
		  put ("Genitiv Singular", "((es$)|(s$)|(n$)|(en$))?");
		  put ("Dativ Singular", "((e$)|(en$))?");
		  put ("Akkusativ Singular", "((en$)|(n$))?");
		  put ("Nominativ Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(er$))?");
		  put ("Genitiv Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(er$))?");
		  put ("Dativ Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(ern$))?");
		  put ("Akkusativ Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(er$))?");		 
		  
		}};
		
		/**
		 * nominal endings
		 */
		public HashMap<String,String> substantiv = new HashMap<String,String>(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				  put( "Nominativ Singular", "");
				  put ("Genitiv Singular", "((es$)|(s$)|(n$)|(en$))?");
				  put ("Dativ Singular", "((e$)|(en$))?");
				  put ("Akkusativ Singular", "((en$)|(n$))?");
				  put ("Nominativ Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(er$))?");
				  put ("Genitiv Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(er$))?");
				  put ("Dativ Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(ern$))?");
				  put ("Akkusativ Plural", "((es$)|(s$)|(en$)|(n$)|(e$)|(er$))?");	
			}
		};
		
		/**
		 * adjectival endings
		 */
		public HashMap<String,String> adjektiv = new HashMap<String,String>(){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		{
			  put ("Positiv", "");
			  put ("Komparativ", "((er$)|(r$))");
			  put ("Superlativ", "((^am\\s)|(esten$)|(sten$))");
		}};
		
		/**
		 * Getter for the regex of the flectional mode in question
		 * @param modus	inflectional mode
		 * @return endings of the mode as regex
		 */
		public String getSubstantivFlexion(String modus) {
			modus = modus.replaceAll("\\*", "");
			
			if (substantiv.containsKey(modus)) {
				return substantiv.get(modus);
			} else {
				return "";
			}
			
		};
		
		/**
		 * Getter for the regex of the flectional mode in question
		 * @param modus	inflectional mode
		 * @return endings of the mode as regex
		 */
		public String getAdjektivFlexion(String modus) {
			
			if (adjektiv.containsKey(modus)) {
				return adjektiv.get(modus);
			} else {
				return "";
			}
		};
		
		/**
		 * Getter for the regex of the flectional mode in question
		 * @param modus	inflectional mode
		 * @return endings of the mode as regex
		 */
		public String getVerbFlexion(String modus) {
			
			if (modus.equals("Indikativ PrÃ¤sens (ich)")  || modus.equals("1. Singular Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_ich";
			}	
			if (modus.equals("Indikativ PrÃ¤sens (du)") || modus.equals("2. Singular Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_du";
			} 
			if (modus.equals("Indikativ PrÃ¤sens (man)")  || modus.equals("3. Singular Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_er, sie, es";
			}
			if (modus.equals("Indikativ PrÃ¤sens (wir)")  || modus.equals("1. Plural Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_wir";
			}
			if (modus.equals("Indikativ PrÃ¤sens (ihr)")  || modus.equals("2. Plural Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_ihr";
			}
			if (modus.equals("Indikativ PrÃ¤sens (sie)")  || modus.equals("3. Plural Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_sie";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (ich)") || modus.equals("1. Singular Konjunktiv PrÃ¤sens Aktiv") || modus.equals("Konjunktiv I_ich")) {
				modus = "Konjunktiv_PrÃ¤sens_ich";
			}	
			if (modus.equals("Konjunktiv PrÃ¤sens (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_du";
			} 
			if (modus.equals("Konjunktiv PrÃ¤sens (man)")|| modus.equals("3. Singular Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_er, sie, es";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (wir)")|| modus.equals("1. Plural Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_wir";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (ihr)")|| modus.equals("2. Plural Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_ihr";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_sie";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum (ich)")  || modus.equals("1. Singular Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_ich";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum (du)") || modus.equals("2. Singular Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_du";
			} 
			if (modus.equals("Indikativ PrÃ¤teritum (man)")  || modus.equals("3. Singular Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_er, sie, es";
			}
			if (modus.equals("Indikativ PrÃ¤teritum (wir)")  || modus.equals("1. Plural Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_wir";
			}
			if (modus.equals("Indikativ PrÃ¤teritum (ihr)")  || modus.equals("2. Plural Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_ihr";
			}
			if (modus.equals("Indikativ PrÃ¤teritum (sie)")  || modus.equals("3. Plural Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_sie";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (ich)") || modus.equals("1. Singular Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_ich";
			}	
			if (modus.equals("Konjunktiv PrÃ¤teritum (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_du";
			} 
			if (modus.equals("Konjunktiv PrÃ¤teritum (man)")|| modus.equals("3. Singular Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_er, sie, es";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (wir)")|| modus.equals("1. Plural Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_wir";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (ihr)")|| modus.equals("2. Plural Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_ihr";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_sie";
			}	
			if (modus.equals("Imperativ (du)") || modus.equals("2. Singular Imperativ PrÃ¤sens Aktiv")) {
				modus = "Imperativ Singular";
			}
			if (modus.equals("Imperativ (ihr)") || modus.equals("2. Plural Imperativ PrÃ¤sens Aktiv")) {
				modus = "Imperativ Plural";
			}
			//Alternativformen
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (ich)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (ich)")|| modus.equals("1. Singular Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ich*";
			}	
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (du)") || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (du)")|| modus.equals("2. Singular Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_du*";
			} 
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (man)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (es)")|| modus.equals("3. Singular Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_er, sie, es*";
			}
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (wir)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (wir)")|| modus.equals("1. Plural Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_wir*";
			}
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (ihr)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (ihr)")|| modus.equals("2. Plural Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ihr*";
			}
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (sie)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (sie)")|| modus.equals("3. Plural Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_sie*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (ich)") ||modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (ich)")|| modus.equals("1. Singular Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ich*";
			}	
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (du)")||modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_du*";
			} 
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (man)")||modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (es)")|| modus.equals("3. Singular Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "Konjunktiv_PrÃ¤sens_er, sie, es*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (wir)")|| modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (wir)")||modus.equals("1. Plural Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_wir*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (ihr)")|| modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (ihr)")||modus.equals("2. Plural Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ihr*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (sie)")|| modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_sie*";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (ich)")  ||modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (ich)")|| modus.equals("1. Singular Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ich*";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (du)") ||modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (du)")|| modus.equals("2. Singular Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_du*";
			} 
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (man)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (es)")|| modus.equals("3. Singular Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_er, sie, es*";
			}
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (wir)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (wir)")|| modus.equals("1. Plural Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_wir*";
			}
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (ihr)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (ihr)")||modus.equals("2. Plural Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ihr*";
			}
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (sie)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (sie)")|| modus.equals("3. Plural Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_sie*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (ich)") || modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (ich)")|| modus.equals("1. Singular Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ich*";
			}	
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (du)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_du*";
			} 
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (man)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (es)")|| modus.equals("3. Singular Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "Konjunktiv_PrÃ¤teritum_er, sie, es*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (wir)")||modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (wir)")|| modus.equals("1. Plural Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_wir*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (ihr)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (ihr)")||modus.equals("2. Plural Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ihr*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (sie)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_sie*";
			}	
			if (modus.equals("Imperativ Alternativform (du)") || modus.equals("2. Singular Imperativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "Imperativ Singular*";
			}
			if (modus.equals("Imperativ Alternativform (ihr)") || modus.equals("2. Plural Imperativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "Imperativ Plural*";
			}				

			if (verb.containsKey(modus)) {
				
				return verb.get(modus);
			} else {
				return "";
			}
			
		}
	},
	
	Deutsch_IPA { 	public HashMap<String,String> verb = new HashMap<String,String>(){
		private static final long serialVersionUID = 1L;
		
		private String eS = "(( É$)|( ə$))";
		private String enS = "(( É n$)|( n̩$))|( ə n$)";
		private String etS = "(( É t$)|( ə t$)|( t$))";
		private String estS = "(( É s t$)|( ə s t$)|( s t$))";
		private String endS = "(( É n t$)|( ə n t$)|( n̩ t$))";
		private String partizip = "((^g É )|(^g É )|(^g e: )|(^g ə )|(^ɡ ə )|( É t$)|( É n$)|( n̩$)|( ə n$)|( ə t$)|( t$))";
		private String teS = "(( É t É$)|( t É$)|( ə t ə$)( t ə$))";
		private String testS = "(( É t É s t$)|( t É s t$)|( ə t ə s t$)|( t ə s t$))";
		private String tenS = "(( É t É n$)|( t É n$)|( ə t ə n$)|( t ə n$)|( t n̩$)|( ə t n̩$))";
		private String tetS = "(( É t É t$)|( t É t$)|( ə t ə t$)|( t ə t$))";
		private String teeS =  "(( É t É$)|( t É$)|( É$)|( ə t ə$)|( t ə$)|( ə$))";
		
		private String genSg = "(( É s$)|( É n$)|( ə s$)|( ə n$)|( n̩$)|( s$)|( n$))?";
		private String datSg = "(( É$)|( É n$)|( ə$)|( ə n$)|( n̩$))?";
		private String accSg = "(( É n$)|( ə n$)|( n̩$)|( n$))?";
		private String plural = "(( É s$)|( É n$)|( É$)|( É r$)|( ə$)|( ə s$)|( ə n$)|( n̩$)|( ə r$)|( ɐ$)|( s$)|( n$))?";
		private String datPl = "(( É s$)|( É n$)|( É$)|( É r n$)|( ə$)|( ə s$)|( ə n$)|( n̩$)|( ə r n$)|( ɐ n$)|( s$)|( n$))?";
		
	{ 
		  put( "PrÃ¤sens_ich", eS ); 
		  put( "PrÃ¤sens_du", estS ); 
		  put( "PrÃ¤sens_er, sie, es", etS ); 
		  put ( "PrÃ¤sens_wir", enS);
		  put("PrÃ¤sens_ihr", etS);
		  put("PrÃ¤sens_sie", enS);
		  put ("Konjunktiv_PrÃ¤sens_du", estS);
		  put ("Konjunktiv_PrÃ¤sens_er, sie, es", eS);
		  put ("Konjunktiv_PrÃ¤sens_ihr", etS);
		  put("Partizip I", endS);
		  put( "Partizip II", partizip ); 
		  put( "PrÃ¤teritum_ich",  teS); 
		  put( "PrÃ¤teritum_du", testS ); 
		  put( "PrÃ¤teritum_er, sie, es", teS ); 
		  put( "PrÃ¤teritum_wir", tenS ); 
		  put( "PrÃ¤teritum_ihr", tetS ); 
		  put( "PrÃ¤teritum_sie", tenS ); 
		  put( "Konjunktiv_PrÃ¤teritum_ich", teS ); 
		  put( "Konjunktiv_PrÃ¤teritum_du", testS ); 
		  put( "Konjunktiv_PrÃ¤teritum_er, sie, es", teS ); 
		  put( "Konjunktiv_PrÃ¤teritum_wir", tenS ); 
		  put( "Konjunktiv_PrÃ¤teritum_ihr", tetS ); 
		  put( "Konjunktiv_PrÃ¤teritum_sie", tenS ); 
		  put( "Konjunktiv II_ich", teeS ); 
		  put( "Imperativ Singular", eS ); 
		  put( "Imperativ Plural", etS );

		  put( "PrÃ¤sens_ich*", eS ); 
		  put( "PrÃ¤sens_du*", estS ); 
		  put( "PrÃ¤sens_er, sie, es*", etS );
		  put ("PrÃ¤sens_wir*", enS);
		  put("PrÃ¤sens_ihr*", etS);
		  put("PrÃ¤sens_sie*", enS);
		  put ("Konjunktiv_PrÃ¤sens_du*", estS);
		  put("Konjunktiv_PrÃ¤sens_er, sie, es*", eS);
		  put ("Konjunktiv_PrÃ¤sens_ihr*", etS);
		  put( "Partizip II*", partizip ); 
		  put( "PrÃ¤teritum_ich*", teS ); 
		  put( "PrÃ¤teritum_du*", testS ); 
		  put( "PrÃ¤teritum_er, sie, es*", teS ); 
		  put( "PrÃ¤teritum_wir*", tenS ); 
		  put( "PrÃ¤teritum_ihr*", tetS ); 
		  put( "PrÃ¤teritum_sie*", tenS ); 
		  put( "Konjunktiv_PrÃ¤teritum_ich*", teS ); 
		  put( "Konjunktiv_PrÃ¤teritum_du*", testS ); 
		  put( "Konjunktiv_PrÃ¤teritum_er, sie, es*", teS ); 
		  put( "Konjunktiv_PrÃ¤teritum_wir*", tenS ); 
		  put( "Konjunktiv_PrÃ¤teritum_ihr*", tetS ); 
		  put( "Konjunktiv_PrÃ¤teritum_sie*", tenS ); 
		  put( "Konjunktiv II_ich*", teeS ); 
		  put( "Imperativ Singular*", eS ); 
		  put( "Imperativ Singular**", eS );
		  put( "Imperativ Plural*", etS );
		  
		  put( "Nominativ Singular", "");
		  put ("Genitiv Singular", genSg);
		  put ("Dativ Singular", datSg);
		  put ("Akkusativ Singular", accSg);
		  put ("Nominativ Plural", plural);
		  put ("Genitiv Plural", plural);
		  put ("Dativ Plural", datPl);
		  put ("Akkusativ Plural", plural);		 
		  
		}};
		

		public HashMap<String,String> substantiv = new HashMap<String,String>(){
			private static final long serialVersionUID = 1L;
			
			private String genSg = "(( É s$)|( s$)|( n$)|( É n$)|( ə s$)|( ə n$)|( n̩$))?";
			private String datSg = "(( É$)|( É n$)|( ə$)|( ə n$)|( n̩$))?";
			private String accSg = "(( É n$)|( ə n$)|( n̩$)|( n$))?";
			private String plural = "(( É s$)|( s$)|( É n$)|( n$)|( É$)|( É r$)|( ə$)|( ə s$)|( ə n$)|( n̩$)|( ə r$)|( ɐ$))?";
			private String datPl = "(( É s$)|( s$)|( É n$)|( n$)|( É$)|( É r n$)|( ə$)|( ə s$)|( ə n$)|( n̩$)|( ə r n$)|( ɐ n$))?";

			{
				  put( "Nominativ Singular", "");
				  put ("Genitiv Singular", genSg);
				  put ("Dativ Singular", datSg);
				  put ("Akkusativ Singular", accSg);
				  put ("Nominativ Plural", plural);
				  put ("Genitiv Plural", plural);
				  put ("Dativ Plural", datPl);
				  put ("Akkusativ Plural", plural);	
			}
		};
		public HashMap<String,String> adjektiv = new HashMap<String,String>(){
			private static final long serialVersionUID = 1L;

			private String komp = "(( É r$)|( ə r$)|( ɐ$)|( r$))";
			private String superlativ = "((^a m )|( É s t É n$)|( s t É n$)|( ə s t ə n$)|( s t ə n$)|( ə s t n̩$)|( s t n̩$))";
		{
			  put ("Positiv", "");
			  put ("Komparativ", komp);
			  put ("Superlativ", superlativ);
		}};
		
		/**
		 * Getter for the regex of the flectional mode in question
		 * @param modus	inflectional mode
		 * @return endings of the mode as regex
		 */
		public String getSubstantivFlexion(String modus) {
			modus = modus.replaceAll("\\*", "");
			
			if (substantiv.containsKey(modus)) {
				return substantiv.get(modus);
			} else {
				return "";
			}
			
		};
		
		/**
		 * Getter for the regex of the flectional mode in question
		 * @param modus	inflectional mode
		 * @return endings of the mode as regex
		 */
		public String getAdjektivFlexion(String modus) {
			
			if (adjektiv.containsKey(modus)) {
				return adjektiv.get(modus);
			} else {
				return "";
			}
		};
		
		/**
		 * Getter for the regex of the flectional mode in question
		 * @param modus	inflectional mode
		 * @return endings of the mode as regex
		 */
		public String getVerbFlexion(String modus) {
			if (modus.equals("Indikativ PrÃ¤sens (ich)")  || modus.equals("1. Singular Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_ich";
			}	
			if (modus.equals("Indikativ PrÃ¤sens (du)") || modus.equals("2. Singular Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_du";
			} 
			if (modus.equals("Indikativ PrÃ¤sens (man)")  || modus.equals("3. Singular Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_er, sie, es";
			}
			if (modus.equals("Indikativ PrÃ¤sens (wir)")  || modus.equals("1. Plural Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_wir";
			}
			if (modus.equals("Indikativ PrÃ¤sens (ihr)")  || modus.equals("2. Plural Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_ihr";
			}
			if (modus.equals("Indikativ PrÃ¤sens (sie)")  || modus.equals("3. Plural Indikativ PrÃ¤sens Aktiv")) {
				modus = "PrÃ¤sens_sie";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (ich)") || modus.equals("1. Singular Konjunktiv PrÃ¤sens Aktiv") || modus.equals("Konjunktiv I_ich")) {
				modus = "Konjunktiv_PrÃ¤sens_ich";
			}	
			if (modus.equals("Konjunktiv PrÃ¤sens (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_du";
			} 
			if (modus.equals("Konjunktiv PrÃ¤sens (man)")|| modus.equals("3. Singular Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_er, sie, es";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (wir)")|| modus.equals("1. Plural Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_wir";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (ihr)")|| modus.equals("2. Plural Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_ihr";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤sens Aktiv")) {
				modus = "Konjunktiv_PrÃ¤sens_sie";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum (ich)")  || modus.equals("1. Singular Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_ich";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum (du)") || modus.equals("2. Singular Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_du";
			} 
			if (modus.equals("Indikativ PrÃ¤teritum (man)")  || modus.equals("3. Singular Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_er, sie, es";
			}
			if (modus.equals("Indikativ PrÃ¤teritum (wir)")  || modus.equals("1. Plural Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_wir";
			}
			if (modus.equals("Indikativ PrÃ¤teritum (ihr)")  || modus.equals("2. Plural Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_ihr";
			}
			if (modus.equals("Indikativ PrÃ¤teritum (sie)")  || modus.equals("3. Plural Indikativ PrÃ¤teritum Aktiv")) {
				modus = "PrÃ¤teritum_sie";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (ich)") || modus.equals("1. Singular Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_ich";
			}	
			if (modus.equals("Konjunktiv PrÃ¤teritum (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_du";
			} 
			if (modus.equals("Konjunktiv PrÃ¤teritum (man)")|| modus.equals("3. Singular Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_er, sie, es";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (wir)")|| modus.equals("1. Plural Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_wir";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (ihr)")|| modus.equals("2. Plural Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_ihr";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤teritum Aktiv")) {
				modus = "Konjunktiv_PrÃ¤teritum_sie";
			}	
			if (modus.equals("Imperativ (du)") || modus.equals("2. Singular Imperativ PrÃ¤sens Aktiv")) {
				modus = "Imperativ Singular";
			}
			if (modus.equals("Imperativ (ihr)") || modus.equals("2. Plural Imperativ PrÃ¤sens Aktiv")) {
				modus = "Imperativ Plural";
			}
			//alternatives
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (ich)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (ich)")|| modus.equals("1. Singular Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ich*";
			}	
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (du)") || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (du)")|| modus.equals("2. Singular Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_du*";
			} 
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (man)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (es)")|| modus.equals("3. Singular Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_er, sie, es*";
			}
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (wir)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (wir)")|| modus.equals("1. Plural Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_wir*";
			}
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (ihr)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (ihr)")|| modus.equals("2. Plural Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ihr*";
			}
			if (modus.equals("Indikativ PrÃ¤sens Alternativform (sie)")  || modus.equals("Indikativ PrÃ¤sens Aktiv Alternativform (sie)")|| modus.equals("3. Plural Indikativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_sie*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (ich)") ||modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (ich)")|| modus.equals("1. Singular Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ich*";
			}	
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (du)")||modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_du*";
			} 
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (man)")||modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (es)")|| modus.equals("3. Singular Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "Konjunktiv_PrÃ¤sens_er, sie, es*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (wir)")|| modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (wir)")||modus.equals("1. Plural Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_wir*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (ihr)")|| modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (ihr)")||modus.equals("2. Plural Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_ihr*";
			}
			if (modus.equals("Konjunktiv PrÃ¤sens Alternativform (sie)")|| modus.equals("Konjunktiv PrÃ¤sens Aktiv Alternativform (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤sens Aktiv Alternativform")) {
				modus = "PrÃ¤sens_sie*";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (ich)")  ||modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (ich)")|| modus.equals("1. Singular Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ich*";
			}	
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (du)") ||modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (du)")|| modus.equals("2. Singular Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_du*";
			} 
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (man)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (es)")|| modus.equals("3. Singular Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_er, sie, es*";
			}
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (wir)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (wir)")|| modus.equals("1. Plural Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_wir*";
			}
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (ihr)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (ihr)")||modus.equals("2. Plural Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ihr*";
			}
			if (modus.equals("Indikativ PrÃ¤teritum Alternativform (sie)")  || modus.equals("Indikativ PrÃ¤teritum Aktiv Alternativform (sie)")|| modus.equals("3. Plural Indikativ PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_sie*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (ich)") || modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (ich)")|| modus.equals("1. Singular Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ich*";
			}	
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (du)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (du)")|| modus.equals("2. Singular Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_du*";
			} 
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (man)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (es)")|| modus.equals("3. Singular Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "Konjunktiv_PrÃ¤teritum_er, sie, es*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (wir)")||modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (wir)")|| modus.equals("1. Plural Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_wir*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (ihr)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (ihr)")||modus.equals("2. Plural Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_ihr*";
			}
			if (modus.equals("Konjunktiv PrÃ¤teritum Alternativform (sie)")|| modus.equals("Konjunktiv PrÃ¤teritum Aktiv Alternativform (sie)")|| modus.equals("3. Plural Konjunktiv PrÃ¤teritum Aktiv Alternativform")) {
				modus = "PrÃ¤teritum_sie*";
			}	
			if (modus.equals("Imperativ Alternativform (du)") || modus.equals("2. Singular Imperativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "Imperativ Singular*";
			}
			if (modus.equals("Imperativ Alternativform (ihr)") || modus.equals("2. Plural Imperativ PrÃ¤sens Aktiv Alternativform")) {
				modus = "Imperativ Plural*";
			}				

			if (verb.containsKey(modus)) {
				
				return verb.get(modus);
			} else {
				return "";
			}
			
		}
	};
}