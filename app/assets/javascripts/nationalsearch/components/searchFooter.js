import LegacySearchLink from "../containers/legacySearchLinkContainer";

const SearchFooter = () => (
    <div>
        <h3 className="heading-medium">Use this new service to search for an offender’s:</h3>

        <ul className="list-bullet margin-top medium">
            <li>name and date of birth</li>
            <li>address and previous addresses</li>
            <li>identification numbers such as CRN, PNC and National Insurance</li>
            <li>aliases and other recorded dates of birth</li>
        </ul>

        <div className="margin-top medium">&nbsp;</div>

        <div>
            <span>You can still access the <LegacySearchLink tabIndex="1">previous version of this search.</LegacySearchLink> </span>
        </div>
    </div>);

export default SearchFooter