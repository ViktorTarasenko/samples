package ru.test.geoname.impl;


import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.MustJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.budetsdelano.startup.entities.Geoname;
import ru.budetsdelano.startup.mobile.json.model.GeonameMarkerModel;
import ru.budetsdelano.startup.server.dao.GeonameService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by victor on 29.04.14.
 */
@Service
public class GeonameServiceImpl implements GeonameService {
    private static final Logger logger = Logger.getLogger(GeonameService.class);
    @Autowired
    private SessionFactory sessionFactory;
    @Transactional(readOnly = true)
    public Geoname findNearestCity(Point location) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Geoname.FIND_NEAREST_CITY);
        query.setParameter("location",location);
        query.setMaxResults(1);
        return query.list().isEmpty() ? null : (Geoname) ((List<Geoname>)query.list()).get(0);
    }
    @Transactional(readOnly = true)
    public Geoname findNearestLocation(Point location) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Geoname.FIND_NEAREST_PLACE);
        query.setParameter("location",location);
        query.setMaxResults(1);
        return query.list().isEmpty() ? null : (Geoname) ((List<Geoname>)query.list()).get(0);
    }

    @Transactional(readOnly = true)
    public List<Geoname> findByAlternateName(String alternatename, int from, int quantity) {
        FullTextSession fullTextSession = Search.getFullTextSession(sessionFactory.getCurrentSession());
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Geoname.class).get();
        org.apache.lucene.search.Query searchQuery = queryBuilder.phrase().withSlop(10).onField("alternatename").andField("alternatenameEdge").andField("alternatenameNgram").sentence(alternatename).createQuery();
        org.apache.lucene.search.Query fclassQuery = queryBuilder.keyword().onField("fclass").matching("P").createQuery();
        org.apache.lucene.search.Query fcode1 = queryBuilder.keyword().onField("fcode").matching("PPLCH").createQuery();
        org.apache.lucene.search.Query fcode2= queryBuilder.keyword().onField("fcode").matching("PPLF").createQuery();
        org.apache.lucene.search.Query fcode3= queryBuilder.keyword().onField("fcode").matching("PPLH").createQuery();
        org.apache.lucene.search.Query fcode4 = queryBuilder.keyword().onField("fcode").matching("PPLQ").createQuery();
        org.apache.lucene.search.Query fcode5 = queryBuilder.keyword().onField("fcode").matching("PPLR").createQuery();
        org.apache.lucene.search.Query fcode6 = queryBuilder.keyword().onField("fcode").matching("PPLW").createQuery();
        org.apache.lucene.search.Query fcode7 = queryBuilder.keyword().onField("fcode").matching("PPLX").createQuery();
        org.apache.lucene.search.Query fcode8 = queryBuilder.keyword().onField("fcode").matching("PPL").createQuery();
        org.apache.lucene.search.Query notFcodeQuery = queryBuilder.bool().should(fcode1).should(fcode2).should(fcode3).should(fcode4).should(fcode5).should(fcode6).should(fcode7).should(fcode8).createQuery();
        notFcodeQuery = queryBuilder.bool().must(notFcodeQuery).not().createQuery();
        org.apache.lucene.search.Query totalFcodeAndFclassQuery = queryBuilder.bool().must(fclassQuery).must(notFcodeQuery).createQuery();
        org.apache.lucene.search.Query totalQuery = queryBuilder.bool().must(searchQuery).must(totalFcodeAndFclassQuery).createQuery();
        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(totalQuery,Geoname.class);
        fullTextQuery.setFirstResult(from);
        fullTextQuery.setMaxResults(quantity);
        return (List<Geoname>)fullTextQuery.list();
    }
    @Transactional(readOnly = true)
    public List<GeonameMarkerModel> findInBounds(double west, double east, double north, double south, double latitude, double longtitude) {
        Query query = sessionFactory.getCurrentSession().getNamedQuery(Geoname.FIND_IN_BOUNDS);
        Point point = new GeometryFactory().createPoint(new Coordinate(longtitude,latitude));
        point.setSRID(4326);
        query.setParameter("center",point);
        final GeometryFactory gf = new GeometryFactory();
        final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
        if (west>east) {
            east = 360 + east;
        }
        if (south > north) {
            north = north + 360;
        }
        points.add(new Coordinate( west ,south));
        points.add(new Coordinate(west , north));
        points.add(new Coordinate(east, north));
        points.add(new Coordinate(east, south));
        points.add(new Coordinate(west, south));
        final Polygon polygon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points
                .toArray(new Coordinate[points.size()])), gf), null);
        polygon.setSRID(4326);
        query.setParameter("bounds", polygon);
        query.setResultTransformer(
          new ResultTransformer() {
              public Object transformTuple(Object[] objects, String[] strings) {

                  GeonameMarkerModel model = new GeonameMarkerModel((Float)objects[0],(Float)objects[1],(BigInteger)objects[2],(Integer)objects[3]);
                  return  model;
              }

              public List transformList(List list) {
                  return list;
              }
          }
        );
        return (List<GeonameMarkerModel>)query.list();
    }

}
