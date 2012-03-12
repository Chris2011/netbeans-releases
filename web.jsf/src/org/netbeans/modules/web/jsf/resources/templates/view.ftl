<#if comment>

  TEMPLATE DESCRIPTION:

  This is XHTML template for 'JSF Pages From Entity Beans' action. Templating
  is performed using FreeMaker (http://freemarker.org/) - see its documentation
  for full syntax. Variables available for templating are:

    entityName - name of entity being modified (type: String)
    managedBean - name of managed choosen in UI (type: String)
    managedBeanProperty - name of managed bean property choosen in UI (type: String)
    item - name of property used for dataTable iteration (type: String)
    comment - always set to "false" (type: Boolean)
    entityDescriptors - list of beans describing individual entities. Bean has following properties:
        label - field label (type: String)
        name - field property name (type: String)
        dateTimeFormat - date/time/datetime formatting (type: String)
        blob - does field represents a large block of text? (type: boolean)
        relationshipOne - does field represent one to one or many to one relationship (type: boolean)
        relationshipMany - does field represent one to many relationship (type: boolean)
        id - field id name (type: String)
        required - is field optional and nullable or it is not? (type: boolean)
        valuesGetter - if item is of type 1:1 or 1:many relationship then use this
            getter to populate <h:selectOneMenu> or <h:selectManyMenu>

  This template is accessible via top level menu Tools->Templates and can
  be found in category JavaServer Faces->JSF from Entity.

</#if>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:h="http://java.sun.com/jsf/html"
      xmlns:f="http://java.sun.com/jsf/core">

    <ui:composition template="/template.xhtml">
        <ui:define name="title">
            <h:outputText value="${r"#{"}bundle.View${entityName}Title${r"}"}"></h:outputText>
        </ui:define>
        <ui:define name="body">
            <h:panelGroup id="messagePanel" layout="block">
                <h:messages errorStyle="color: red" infoStyle="color: green" layout="table"/>
            </h:panelGroup>
            <h:form>
                <h:panelGrid columns="2">
<#list entityDescriptors as entityDescriptor>
                    <h:outputText value="${r"#{"}bundle.View${entityName}Label_${entityDescriptor.id?replace(".","_")}${r"}"}"/>
    <#if entityDescriptor.dateTimeFormat?? && entityDescriptor.dateTimeFormat != "">
                    <h:outputText value="${r"#{"}${entityDescriptor.name}${r"}"}" title="${r"#{"}bundle.View${entityName}Title_${entityDescriptor.id?replace(".","_")}${r"}"}">
                        <f:convertDateTime pattern="${entityDescriptor.dateTimeFormat}" />
                    </h:outputText>
    <#else>
                    <h:outputText value="${r"#{"}${entityDescriptor.name}${r"}"}" title="${r"#{"}bundle.View${entityName}Title_${entityDescriptor.id?replace(".","_")}${r"}"}"/>
    </#if>
</#list>
                </h:panelGrid>
                <br />
                <h:commandLink action="${r"#{"}${managedBean}${r".destroyAndView}"}" value="${r"#{"}bundle.View${entityName}DestroyLink${r"}"}"/>
                <br />
                <br />
                <h:link outcome="Edit" value="${r"#{"}bundle.View${entityName}EditLink${r"}"}"/>
                <br />
                <h:commandLink action="${r"#{"}${managedBean}${r".prepareCreate}"}" value="${r"#{"}bundle.View${entityName}CreateLink${r"}"}" />
                <br />
                <h:commandLink action="${r"#{"}${managedBean}${r".prepareList}"}" value="${r"#{"}bundle.View${entityName}ShowAllLink${r"}"}"/>
                <br />
                <br />
                <h:link outcome="/index" value="${r"#{"}bundle.View${entityName}IndexLink${r"}"}"/>

            </h:form>
        </ui:define>
    </ui:composition>

</html>
