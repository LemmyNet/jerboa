package com.jerboa.datatypes.types

data class SiteView(
    var site: Site,
    var local_site: LocalSite,
    var local_site_rate_limit: LocalSiteRateLimit,
    var counts: SiteAggregates,
)