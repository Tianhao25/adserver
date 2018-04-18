package io.bittiger.ads.ad.services;
import io.bittiger.ads.support.Utility;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryParser {
	public List<String> QueryUnderstand(String query) {
		List<String> tokens = Utility.cleanedTokenize(query);
		return tokens;
	}
}
