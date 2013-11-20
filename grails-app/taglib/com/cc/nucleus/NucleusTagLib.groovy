package com.cc.nucleus

class NucleusTagLib {

    static namespace = "nucleus"

    static defaultEncodeAs = "raw"

    /**
     * @attr total REQUIRED total number of instances
     */
    def pagerInfo = { attrs, body ->
        long total = attrs.total
        if(!total) {
            out << """<i class="fa fa-meh-o"></i> Sorry, no records found."""
            return
        }
        params.offset = params.offset ?: 0
        long limit = params.offset.toInteger() + params.max.toInteger()

        out << """<small>
                      Showing: <strong>${params.offset.toInteger() + 1 }-${limit > total ? total : limit }</strong>
                      of <strong>${total }</strong>
                  </small>
               """
    }

}