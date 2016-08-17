package org.openstreetmap.josm.plugins.ods.bag.osm.build;

public class BagOsmEntityBuilder {
    

//    public static void parseKeys(Entity entity, Map<String, String> tags) {
//        entity.setReferenceId(getReferenceId(tags.remove("ref:bag")));
//        entity.setSource(tags.remove("source"));
//        String sourceDate = tags.remove("source:date");
//        if (sourceDate != null) {
//            try {
//                entity.setSourceDate(LocalDate.parse(sourceDate));
//            } catch (@SuppressWarnings("unused") DateTimeParseException e) {
//                // TODO Auto-generated catch block
//            }
//        }
//    }
    
    static Long getReferenceId(String s) {
        if (s == null || s.length() == 0) return null;
        int i=0;
        while (i<s.length() && Character.isDigit(s.charAt(i))) {
            i++;
        }
        return Long.valueOf(s.substring(0, i));
    }
}
