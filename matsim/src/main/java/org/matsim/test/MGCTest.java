package org.matsim.test;

import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.matsim.core.utils.geometry.geotools.MGC;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.IOException;
import java.io.*;
public class MGCTest {

    @Test
    public void test() throws FactoryException{
        String krovakWKT = "PROJCS[\"S-JTSK / Krovak East North\",GEOGCS[\"S-JTSK\",DATUM[\"System_Jednotne_Trigonometricke_Site_Katastralni\",SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],TOWGS84[485.0,169.5,483.8,7.786,4.398,4.103,0],AUTHORITY[\"EPSG\",\"6156\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.0174532925199433,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4156\"]],PROJECTION[\"Krovak\"],PARAMETER[\"latitude_of_center\",49.5],PARAMETER[\"longitude_of_center\",24.83333333333333],PARAMETER[\"azimuth\",30.28813972222222],PARAMETER[\"pseudo_standard_parallel_1\",78.5],PARAMETER[\"scale_factor\",0.9999],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"metre\",1,AUTHORITY[\"EPSG\",\"9001\"]],AXIS[\"X\",EAST],AXIS[\"Y\",NORTH],AUTHORITY[\"EPSG\",\"5514\"]]";
        try{
            CoordinateReferenceSystem sourceCRS = CRS.parseWKT(krovakWKT);
            //CoordinateReferenceSystem targetCRS = MGC.getCRS("EPSG:102066");
            Assert.assertNotNull(sourceCRS);
            System.out.print(sourceCRS);
        } catch(FactoryException ex){
            return;
        }

    }
}