<soap:Envelope
        xmlns:soap="http://www.w3.org/2003/05/soap-envelope/"
        soap:encodingStyle="http://www.w3.org/2003/05/soap-encoding">

    <soap:Body xmlns:m="http://www.example.org/stock">
        <m:GetBookRequest>
            <m:BookName>${model.name}</m:BookName>
        </m:GetBookRequest>
    </soap:Body>

</soap:Envelope>