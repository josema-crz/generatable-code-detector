/*
 * Copyright (c) 2013 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package similaritycalculation.gst.asttokenizer;

import java.util.HashSet;
import java.util.Set;

/**
 * Configuration class for templification settings.
 * 
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class TemplificationConfiguration {
  
  private Set<TokenSymbols_Step10> tokenSymbolsToBeFiltered;
  
  private static TemplificationConfiguration defaultConfiguration;
  
  public TemplificationConfiguration() {
    this.tokenSymbolsToBeFiltered = new HashSet<TokenSymbols_Step10>();
  }
  
  public static TemplificationConfiguration createDefaultConfiguration() {
    if (defaultConfiguration != null) {
      return defaultConfiguration;
    }
    
    defaultConfiguration = new TemplificationConfiguration();
    defaultConfiguration.addTokenSymbolsToBeFiltered(TokenSymbols_Step10.PACKAGE_DECLARATION);
    
    return defaultConfiguration;
  }  
  
  public void addTokenSymbolsToBeFiltered(TokenSymbols_Step10 symbol) {
    this.tokenSymbolsToBeFiltered.add(symbol);
  }
  
  public boolean shouldTokenSymbolBeFiltered(TokenSymbols_Step10 symbol) {
    return this.tokenSymbolsToBeFiltered.contains(symbol);
  }
}
