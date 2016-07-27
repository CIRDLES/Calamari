/*
 * Copyright 2016 CIRDLES
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cirdles.calamari.web;

import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * Created by johnzeringue on 7/27/16.
 */
@Path("prawn")
public class PrawnResource {

    private PrawnFileHandlerService prawnFileHandlerService;

    public PrawnResource() {
        prawnFileHandlerService = new PrawnFileHandlerService();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces("application/zip")
    public Response generateReports(
            @FormDataParam("prawnFile") InputStream prawnFile,
            @DefaultValue("true") @PathParam("useSBM") boolean useSBM,
            @DefaultValue("false") @PathParam("userLinFits") boolean userLinFits)
            throws Exception {

        return Response
                .ok(prawnFileHandlerService
                        .generateReports(prawnFile, useSBM, userLinFits)
                        .toFile())
                .header("Content-Disposition",
                        "attachment; filename=calamari-reports.zip")
                .build();
    }

}
