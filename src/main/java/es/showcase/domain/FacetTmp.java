package es.showcase.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author: yearsaaaa
 */
public class FacetTmp {

    private String[] types = new String[]{"type1","type2","type3","type4","type5","type6","type7","type8","type9","type10",
                                          "type11","type12","type13","type14","type15","type16","type17","type18","type19","type20"};
    private long id;
    private String type;
    private List<Integer> catIds;

    public FacetTmp(){
        Random r = new Random();
        int n = Math.abs(r.nextInt());
        int index = n%14;
        this.type = types[index];
        this.id = Math.abs(r.nextLong());

        n = n%50;
        catIds = new ArrayList<Integer>();
        catIds.add(n);
        int ys = n%3;
        if(ys!=0){
            for(int i=1;i<ys+1;i++){
                catIds.add(n+i);
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Integer> getCatIds() {
        return catIds;
    }

    public void setCatIds(List<Integer> catIds) {
        this.catIds = catIds;
    }
}
