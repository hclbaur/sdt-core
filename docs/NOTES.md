## XSLT vs SDT

### choose

	<choose>
	  <when test="[expression]">
		...
	  </when>
	  <otherwise>
		...
	  </otherwise>
	</choose>

	choose {
	  when "[expression]" {
		...
	  }
	  otherwise {
		...
	  }
	}
	
### copy-of

	<copy-of select="[expression]" />

	copy { select "[expression]" }

### element

    <element name="[name]">
      <value-of select="[expression]" />
    </element>

	node "[name]" { 
		value "[expression]" 
	}
	
### for-each

    <for-each select="[expression]">
      ...
    </for-each>

	foreach "[expression]" {
		...
	}

### if

	<if test="[expression]">
		...
	</if>

	if "[expression]" {
		...
	}

### param

	<param name="[name]" select="[expression]" />

	param "[name]" { select "[expression]" }

### sort

	<sort select="[expression]" lang="..." data-type="..." order="..." case-order="..." />

	sort "[expression]" { reverse "[expression]" comparator "[function]" }


### stylesheet (transform)

	<stylesheet> ... </stylesheet>   (<transform> ... </transform>)

	transform { ... }

### variable

	<variable name="[name]" select="[expression]" />
	
	variable "[name]" { select "[expression]" }

