package com.platypus.android.tablet;

/**
 * Created by shenty on 2/3/16.
 */

import java.util.ArrayList;
import java.util.List;
import com.mapbox.mapboxsdk.geometry.LatLng;

/* note
* Should not use yet for following reasons
* Points have to be in CLOCKWISE order (TODO)
* If polygon intersects itself wont work
* */



public class PolyArea
{
    ArrayList<LatLng> vertices; //vertex
    ArrayList<LatLng> vectors;  //lines (vectors)
    boolean convex;
    LatLng centroid;

    public PolyArea(ArrayList<LatLng> polyPointList)
    {
        vertices = polyPointList;
        vectors = generateVectors(vertices); //compute vectors
        convex = isConvex(vectors); //cross each vector check sign,
        centroid = computeCentroid();
        //determine if convex or not

    }
    public ArrayList<LatLng> generateVectors(List<LatLng> polyPointList)
    {
        ArrayList<LatLng> temp = new ArrayList<LatLng>();
        //point = {x,y}
        for (int i = 0; i < polyPointList.size()-1; i++)
        {
            double pointx = -polyPointList.get(i).getLatitude() + polyPointList.get(i+1).getLatitude();
            double pointy = -polyPointList.get(i).getLongitude() + polyPointList.get(i+1).getLongitude() ;
            temp.add(new LatLng(pointx,pointy));
        }
        double pointx =  polyPointList.get(0).getLatitude()- polyPointList.get(polyPointList.size()-1).getLatitude();
        double pointy =  polyPointList.get(0).getLongitude() - polyPointList.get(polyPointList.size()-1).getLongitude();
        temp.add(new LatLng(pointx,pointy));
        return temp;
    }

    //POINTS HAVE TO BE IN ORDER (clockwise) :|
    public boolean isConvex(List<LatLng> vectors)
    {
        boolean neg = false;
        boolean pos = false;;
        for (int i = 0; i < vectors.size()-1; i++)
        {
            LatLng vector1 = vectors.get(i);
            LatLng vector2 = vectors.get(i+1);
            double tempcross = crossZ(vector1,vector2);
            if (tempcross > 0)
            {
                pos = true;
            }
            else
            {
                neg = true;
            }
        }
        LatLng vector1 = vectors.get(0);
        LatLng vector2 = vectors.get(vectors.size()-1);
        double tempcross = crossZ(vector2,vector1);
        if (tempcross > 0)
        {
            pos = true;
        }
        else
        {
            neg = true;
        }
        if (pos  == true && neg == true)
        {
            convex = false;
            return false;
        }
        convex = true;
        return true;
    }

    //if all in clockwise order all cross product signs should be negative
    public boolean isConcaveAngle (LatLng vector1, LatLng vector2)
    {
        if (crossZ(vector1,vector2) > 0)
        {
            return true;
        }
        return false;
    }

    /* get z component of cross product */
    public static double crossZ(LatLng u, LatLng v)
    {
        return u.getLatitude()*v.getLongitude() - v.getLatitude()*u.getLongitude();
    }

    public void makeConvex()
    {
        while(isConvex(vectors) == false)
        {
            // System.out.println("");
            // printVertices();

            for (int i = 0; i < vectors.size()-1; i++)
            {
                if (i == vectors.size()-2)
                {
                    if(isConcaveAngle(vectors.get(vectors.size()-1),vectors.get(0)) == true)
                    {
                        vertices.remove(0);
                        vectors = generateVectors(vertices);
                        break;
                    }
                }

                else if(isConcaveAngle(vectors.get(i),vectors.get(i+1)) == true)
                {
                    vertices.remove(i+1); //remove vertex
                    vectors = generateVectors(vertices); //recreate
                    //new vectors
                    break;
                }
            }
        }
        convex = isConvex(vectors);
    }
    public void printVertices()
    {
        for (LatLng i : vertices)
        {
            System.out.println(i.getLatitude() + " " + i.getLongitude());
        }
    }
    public LatLng computeCentroid()
    {
        double tempLat = 0;
        double tempLon = 0;

        for (LatLng i : vertices)
        {
            tempLat += i.getLatitude();
            tempLon += i.getLongitude();
        }
        return new LatLng(tempLat/vertices.size(),tempLon/vertices.size());
    }
}

