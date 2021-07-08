package ch.admin.bag.covidcertificate.service.encoder;

import lombok.extern.slf4j.Slf4j;
import se.digg.dgc.encoding.BarcodeCreator;
import se.digg.dgc.encoding.Base45;
import se.digg.dgc.encoding.Zlib;
import se.digg.dgc.service.impl.DefaultDGCBarcodeEncoder;
import se.digg.dgc.signatures.DGCSigner;

import java.io.IOException;
import java.security.SignatureException;
import java.time.Instant;

import static ch.admin.bag.covidcertificate.api.Constants.LIGHT_CERTIFICATE_HEADER;

@Slf4j
public class CertificateLightBarcodeEncoder extends DefaultDGCBarcodeEncoder {

    public CertificateLightBarcodeEncoder(DGCSigner dgcSigner, BarcodeCreator barcodeCreator) {
        super(dgcSigner, barcodeCreator);
    }

    @Override
    public String encode(final byte[] dcc, final Instant expiration) throws IOException, SignatureException {

        log.trace("Encoding to Base45 from CBOR-encoded DCC-payload (length: {}) ...", dcc.length);

        // Create a signed CWT ...
        //
        byte[] cwt = this.sign(dcc, expiration);

        // Compression and Base45 encoding ...
        //
        log.trace("Compressing the signed CWT of length {} ...", cwt.length);
        cwt = Zlib.compress(cwt);
        log.trace("Signed CWT was compressed into {} bytes", cwt.length);

        log.trace("Base45 encoding compressed CWT ...");
        final String base45 = Base45.getEncoder().encodeToString(cwt);
        log.trace("Base45 encoding: {}", base45);

        return LIGHT_CERTIFICATE_HEADER + base45;
    }
}
