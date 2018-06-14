const SearchFooter = () => (
    <div>
        <div className="background-box">
            <div className="key-content">
                <p className="font-medium margin-bottom">
                    Narrow <span className="bold">your results</span> by selecting <span className="bold">'Yes'</span> to <span className="bold">'Match all terms'</span>
                </p>
            </div>
        </div>

        <div className="key-content">
            <h3 className="heading-medium">
                Find an offender by using any combination of:
            </h3>
            <ul className="list-bullet margin-top medium">
                <li>name and date of birth</li>
                <li>address and previous addresses</li>
                <li>CRN, PNC and National Insurance numbers</li>
                <li>aliases and other recorded dates of birth</li>
            </ul>
        </div>

    </div>
);

export default SearchFooter