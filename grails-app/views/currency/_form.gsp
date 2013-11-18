<g:hasErrors bean="${currencyInstance}">
    <ul class="text-error">
        <g:eachError bean="${currencyInstance}" var="error">
            <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>>
                <g:message error="${error}" /></li>
        </g:eachError>
    </ul>
</g:hasErrors>

<div class="form-group ${hasErrors(bean: currencyInstance, field: 'code', 'error')}">
    <label class="control-label col-lg-2" for="code">
        <g:message code="currency.code.label" default="Code" />
    </label>
    <div class="col-lg-4">
        <g:textField name="code" required="" value="${currencyInstance?.code}" autofocus="" class="form-control"/>
    </div>
</div>

<div class="form-group ${hasErrors(bean: currencyInstance, field: 'name', 'error')}">
    <label class="control-label col-lg-2" for="name">
        <g:message code="currency.name.label" default="Name" />
    </label>
    <div class="col-lg-4">
        <g:textField name="name" required="" value="${currencyInstance?.name}" class="form-control"/>
    </div>
</div>

