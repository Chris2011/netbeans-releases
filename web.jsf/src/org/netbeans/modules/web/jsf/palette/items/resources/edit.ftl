<#if comment>

  TEMPLATE DESCRIPTION:

  This is XHTML template for 'JSF Editable Form From Entity' action. Templating
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
  be found in category JavaServer Faces->JSF Data/Form from Entity.

</#if>
<h:form>
    <h1><h:outputText value="Create/Edit"/></h1>
    <h:panelGrid columns="2">
<#list entityDescriptors as entityDescriptor>
        <h:outputLabel value="${entityDescriptor.label}:" for="${entityDescriptor.id}" />
<#if entityDescriptor.dateTimeFormat?? && entityDescriptor.dateTimeFormat != "">
        <h:inputText id="${entityDescriptor.id}" value="${r"#{"}${entityDescriptor.name}${r"}"}" title="${entityDescriptor.label}" <#if entityDescriptor.required>required="true" requiredMessage="The ${entityDescriptor.label} field is required."</#if>>
            <f:convertDateTime pattern="${entityDescriptor.dateTimeFormat}" />
        </h:inputText>
<#elseif entityDescriptor.blob>
        <h:inputTextarea rows="4" cols="30" id="${entityDescriptor.id}" value="${r"#{"}${entityDescriptor.name}${r"}"}" title="${entityDescriptor.label}" <#if entityDescriptor.required>required="true" requiredMessage="The ${entityDescriptor.label} field is required."</#if>/>
<#elseif entityDescriptor.relationshipOne>
        <h:selectOneMenu id="${entityDescriptor.id}" value="${r"#{"}${entityDescriptor.name}${r"}"}" title="${entityDescriptor.label}" <#if entityDescriptor.required>required="true" requiredMessage="The ${entityDescriptor.label} field is required."</#if>>
            <!-- TODO: update below reference to list of available items-->
            <f:selectItems value="${r"#{"}fixme${r"}"}"/>
        </h:selectOneMenu>
<#elseif entityDescriptor.relationshipMany>
        <h:selectManyMenu id="${entityDescriptor.id}" value="${r"#{"}${entityDescriptor.name}${r"}"}" title="${entityDescriptor.label}" <#if entityDescriptor.required>required="true" requiredMessage="The ${entityDescriptor.label} field is required."</#if>>
            <!-- TODO: update below reference to list of available items-->
            <f:selectItems value="${r"#{"}fixme${r"}"}"/>
        </h:selectManyMenu>
<#else>
        <h:inputText id="${entityDescriptor.id}" value="${r"#{"}${entityDescriptor.name}${r"}"}" title="${entityDescriptor.label}" <#if entityDescriptor.required>required="true" requiredMessage="The ${entityDescriptor.label} field is required."</#if>/>
</#if>
</#list>
    </h:panelGrid>
</h:form>
