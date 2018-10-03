import FeedbackLink from './feedbackLink';
import LegacySearchLink from '../containers/legacySearchLinkContainer';

const GovUkPhaseBanner = () => {
    return (
        <div className="phase-banner phase-banner-boxed mobile-pad no-cell key-content"><p><strong className="phase-tag">BETA</strong><span>This is a new service – your <FeedbackLink>feedback</FeedbackLink> will help us to improve it.&nbsp;</span>Access the <LegacySearchLink tabIndex="1">previous search</LegacySearchLink> here.<span/></p></div>
    )
};

export default GovUkPhaseBanner;