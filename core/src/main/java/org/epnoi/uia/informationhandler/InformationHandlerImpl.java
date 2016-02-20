package org.epnoi.uia.informationhandler;

import org.epnoi.informationhandler.wrappers.Wrapper;
import org.epnoi.informationhandler.wrappers.WrapperFactory;
import org.epnoi.model.Content;
import org.epnoi.model.Context;
import org.epnoi.model.Resource;
import org.epnoi.model.Selector;
import org.epnoi.model.exceptions.EpnoiInitializationException;
import org.epnoi.model.modules.*;
import org.epnoi.model.parameterization.*;
import org.epnoi.uia.informationstore.InformationStoreFactory;
import org.epnoi.uia.informationstore.SelectorHelper;
import org.epnoi.uia.informationstore.VirtuosoInformationStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Component
public class InformationHandlerImpl implements InformationHandler {
    private static final Logger LOG = LoggerFactory.getLogger(InformationHandlerImpl.class);
    
    @Autowired
    private Core core;

    @Autowired
    private ParametersModel parameters;
    private WrapperFactory wrapperFactory;

    private List<InformationAccessListener> listeners;

    private HashMap<String, InformationStore> informationStores;
    private HashMap<String, List<InformationStore>> informationStoresByType;

    volatile boolean initialized;

    // ---------------------------------------------------------------------------

    public InformationHandlerImpl() {
        this.initialized = false;
    }

    @Override
    public boolean checkStatus(String informationStoreURI) {
        InformationStore informationStore = this.informationStores.get(informationStoreURI);
        return informationStore.test();
    }


    @PostConstruct
    public synchronized void init() throws EpnoiInitializationException {
        if (!this.initialized) {
            LOG.info("Initializing the Information Handler");
            this.wrapperFactory = new WrapperFactory(core);
            this.listeners = new ArrayList<InformationAccessListener>();
            this.informationStores = new HashMap<>();
            this.informationStoresByType = new HashMap<>();
            this._informationStoresInitialization();
            this.initialized = checkInitialization();
        } else {
            throw new EpnoiInitializationException("Error trying to reinitialize the Information Handler");
        }
    }

    private boolean checkInitialization() {
        return ((this.initialized = this.getInformationStoresByType(
                InformationStoreHelper.MAP_INFORMATION_STORE) != null) && (this.getInformationStoresByType(
                InformationStoreHelper.SOLR_INFORMATION_STORE) != null) && (this.getInformationStoresByType(
                InformationStoreHelper.RDF_INFORMATION_STORE) != null) && (this.getInformationStoresByType(
                InformationStoreHelper.CASSANDRA_INFORMATION_STORE) != null));
    }

    // ---------------------------------------------------------------------------


    public void close() {
        for (InformationStore dataSource : this.informationStores.values()) {
            dataSource.close();
        }

    }

    public void update(Resource resource) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(resource);
            wrapper.update(resource);
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. No information was updated regarding the resource "+resource.getUri());
        }
    }

    // ---------------------------------------------------------------------------

    public void put(Resource resource, Context context) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(resource);
            wrapper.put(resource, context);
            resource = null;
            context.clear();
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. No information was updated regarding the resource "+resource.getUri());
        }

    }

    // ---------------------------------------------------------------------------

    public Resource get(String URI) {
        // TODO: As it is now it just delivers items/feeds
        Resource resource = null;
        if (this.initialized) {
            String resourceType = this.getType(URI);
            if (resourceType != null) {

                Wrapper wrapper = this.wrapperFactory.build(resourceType);
                resource = wrapper.get(URI);
            } else {
                LOG.error("The type " + resourceType + " cannot be resolved as a valid type. A null is returned");

            }
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. A null is returned when retrieving "+URI);

        }
        return resource;
    }

    // ---------------------------------------------------------------------------

    public Resource get(String URI, String resourceType) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(resourceType);
            return wrapper.get(URI);
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. A null is returned when retrieving "+URI);
            return null;
        }
    }

    // ---------------------------------------------------------------------------

    public void remove(String URI, String resourceType) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(resourceType);
            wrapper.remove(URI);
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. No information was updated with regarding "+URI);
        }
    }

    // ---------------------------------------------------------------------------

    public void remove(Resource resource) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(resource);
            wrapper.remove(resource.getUri());
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. No information was updated regarding "+resource.getUri());
        }
    }

    // ---------------------------------------------------------------------------


    // ---------------------------------------------------------------------------

    public synchronized void publish(String eventType, Resource source) {
        for (InformationAccessListener listener : this.listeners) {
            listener.notify(eventType, source);
        }
    }

    // ---------------------------------------------------------------------------

    public synchronized void subscribe(InformationAccessListener listener,
                                       String subscriptionExpression) {
        this.listeners.add(listener);
    }

    // ---------------------------------------------------------------------------

    @Override
    public Content<String> getContent(Selector selector) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(selector
                    .getProperty(SelectorHelper.TYPE));
            Content<String> content = wrapper.getContent(selector);

            return content;
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. A null is returned when requesting "+selector);
            return null;
        }
    }

    // ---------------------------------------------------------------------------

    @Override
    public Content<Object> getAnnotatedContent(Selector selector) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(selector
                    .getProperty(SelectorHelper.TYPE));

            Content<Object> content = wrapper.getAnnotatedContent(selector);
            return content;
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. A null is returned when requesting "+selector);
            return null;
        }
    }

    // ---------------------------------------------------------------------------

    @Override
    public void setContent(Selector selector, Content<String> content) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(selector
                    .getProperty(SelectorHelper.TYPE));
            wrapper.setContent(selector, content);
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. No information was updated regarding "+selector);
        }
    }

    // ---------------------------------------------------------------------------

    @Override
    public void setAnnotatedContent(Selector selector,
                                    Content<Object> annotatedContent) {
        if (this.initialized) {
            Wrapper wrapper = this.wrapperFactory.build(selector
                    .getProperty(SelectorHelper.TYPE));

            wrapper.setAnnotatedContent(selector, annotatedContent);
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. No information was updated regarding "+selector);
        }
    }

    // ---------------------------------------------------------------------------

    @Override
    public boolean contains(String URI, String resourceType) {
        if (this.initialized) {
            Selector selector = new Selector();
            selector.setProperty(SelectorHelper.TYPE, resourceType);
            selector.setProperty(SelectorHelper.URI, URI);
            Wrapper wrapper = this.wrapperFactory.build(resourceType);
            return wrapper.exists(URI);

        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. A default false value has been returned when asking for "+URI);
            return false;
        }
    }

    // ---------------------------------------------------------------------------

    @Override
    public List<String> getAll(String resourceType) {
        // ------------------------------------------------------------------------------
        if (this.initialized) {
            InformationStore informationStore = this.core.getInformationHandler()
                    .getInformationStoresByType(
                            InformationStoreHelper.RDF_INFORMATION_STORE).get(0);

            String queryExpression = "SELECT DISTINCT ?uri FROM <{GRAPH}>"
                    + "{ ?uri a <" + resourceType + "> ." + "}";

            queryExpression = queryExpression
                    .replace(
                            "{GRAPH}",
                            ((VirtuosoInformationStoreParameters) informationStore
                                    .getParameters()).getGraph());

            System.out.println("QUERY EXPRESSION ----------> " + queryExpression);
            List<String> queryResults = informationStore.query(queryExpression);

            return queryResults;
        } else {
            LOG.error("The InformationStores handled by the InformationHandler were not properly initialized. A simple empty list has been returned when getting the resources of type "+resourceType);
            return new ArrayList<>();
        }
    }

    // ---------------------------------------------------------------------------

    public String getType(String URI) {
        VirtuosoInformationStore informationStore = (VirtuosoInformationStore) this.core.getInformationHandler()
                .getInformationStoresByType(
                        InformationStoreHelper.RDF_INFORMATION_STORE).get(0);

        return informationStore.getType(URI);
    }


    /**
     * Information Stores initialization
     */

    private void _informationStoresInitialization() {

        LOG.info("Initializing information stores");
        LOG.info("Initializing Virtuoso information stores");
        for (VirtuosoInformationStoreParameters virtuosoInformationStoreParameters : this.parameters
                .getVirtuosoInformationStore()) {
            _initVirtuosoInformationStore(virtuosoInformationStoreParameters);

        }
        LOG.info("Initializing SOLR information stores");
        for (SOLRInformationStoreParameters solrInformationStoreParameters : this.parameters
                .getSolrInformationStore()) {
            _initSOLRInformationStore(solrInformationStoreParameters);

        }
        LOG.info("Initializing Cassandra information stores");
        for (CassandraInformationStoreParameters cassandraInformationStoreParameters : this.parameters
                .getCassandraInformationStore()) {
            _initCassandraInformationStore(cassandraInformationStoreParameters);

        }
        LOG.info("Initializing map information stores");
        for (MapInformationStoreParameters mapInformationStoreParameters : this.parameters.getMapInformationStore()) {
            _initMapInformationStore(mapInformationStoreParameters);

        }

    }

    private void _initMapInformationStore(MapInformationStoreParameters mapInformationStoreParameters) {
        LOG.info(mapInformationStoreParameters.toString());
        InformationStore newInformationStore = null;
        try {
            newInformationStore = InformationStoreFactory
                    .buildInformationStore(mapInformationStoreParameters, this.parameters);
            LOG.info("The status of the information source is " + newInformationStore.test());
        } catch (Exception e) {
            LOG.error("Something went wrong in the MapInfomration store",e);
        }
        this.informationStores.put(mapInformationStoreParameters.getURI(), newInformationStore);

        _addInformationStoreByType(newInformationStore, InformationStoreHelper.MAP_INFORMATION_STORE);

    }

    private void _initCassandraInformationStore(CassandraInformationStoreParameters cassandraInformationStoreParameters) {
        LOG.info(cassandraInformationStoreParameters.toString());

        InformationStore newInformationStore = null;
        try {
            newInformationStore = InformationStoreFactory
                    .buildInformationStore(cassandraInformationStoreParameters, this.parameters);
            LOG.info("The status of the information source is " + newInformationStore.test());
        } catch (Exception e) {
            LOG.error("Something went wrong in the CassandraInformationStore initialization!",e);
        }
        this.informationStores.put(cassandraInformationStoreParameters.getURI(), newInformationStore);

        _addInformationStoreByType(newInformationStore, InformationStoreHelper.CASSANDRA_INFORMATION_STORE);

    }

    private void _initSOLRInformationStore(SOLRInformationStoreParameters solrInformationStoreParameters) {
        LOG.info(solrInformationStoreParameters.toString());
        InformationStore newInformationStore = null;
        try {
            newInformationStore = InformationStoreFactory
                    .buildInformationStore(solrInformationStoreParameters, this.parameters);
            LOG.info("The status of the information source is " + newInformationStore.test());
        } catch (Exception e) {
            LOG.error("Something went wrong in the SOLRInformationStore initialization!",e);
        }
        this.informationStores.put(solrInformationStoreParameters.getURI(), newInformationStore);

        _addInformationStoreByType(newInformationStore, InformationStoreHelper.SOLR_INFORMATION_STORE);

    }

    private void _initVirtuosoInformationStore(VirtuosoInformationStoreParameters virtuosoInformationStoreParameters) {
        LOG.info(virtuosoInformationStoreParameters.toString());
        InformationStore newInformationStore = null;
        try {
            newInformationStore = InformationStoreFactory
                    .buildInformationStore(virtuosoInformationStoreParameters, this.parameters);
            LOG.info("The status of the information source is " + newInformationStore.test());
        } catch (Exception e) {
            LOG.error("Something went wrong in the VirtuosoInfomrationStore initialization!",e);
        }
        this.informationStores.put(virtuosoInformationStoreParameters.getURI(), newInformationStore);

        _addInformationStoreByType(newInformationStore, InformationStoreHelper.RDF_INFORMATION_STORE);


    }

    // ----------------------------------------------------------------------------------------------------------

    private void _addInformationStoreByType(InformationStore informationStore, String type) {
        List<InformationStore> informationStoresOfType = this.informationStoresByType.get(type);
        if (informationStoresOfType == null) {
            informationStoresOfType = new ArrayList<InformationStore>();
            this.informationStoresByType.put(type, informationStoresOfType);
        }
        informationStoresOfType.add(informationStore);
    }


    // ----------------------------------------------------------------------------------------------------------

    @Override
    public Collection<InformationStore> getInformationStores() {
        return this.informationStores.values();
    }

    // ----------------------------------------------------------------------------------------------------------

    @Override
    public List<InformationStore> getInformationStoresByType(String type) {
        return this.informationStoresByType.get(type);
    }

}
