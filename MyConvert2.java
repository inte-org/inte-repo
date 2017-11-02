/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/

package org.compiere.process;
import java.awt.print.Book;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.adempiere.exceptions.FillMandatoryException;
import org.compiere.model.MBPartner;
import org.compiere.model.MBPartnerLocation;
import org.compiere.model.MBooking_New;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoice_New;
import org.compiere.model.MLocation;
import org.compiere.model.MOpportunity;


import org.compiere.model.PO;
import org.compiere.process.ProcessInfoParameter;
import org.compiere.process.SvrProcess;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;  
   
  
      

/**
 * 
 * Convert mybooking into business partner and opportunity
 * @author Paul Bowden, Adaxa Pty Ltd
 *
 */
public class MyConvert2 extends SvrProcess {

	private int p_Sh_Booking_ID = 0;
	@Override
	protected String doIt() throws Exception {
		
		PreparedStatement pst=null;
		ResultSet rs=null;
		ResultSet rs2=null;  
		
		if (p_Sh_Booking_ID <= 0)
			throw new FillMandatoryException("Sh_Booking_ID");
		
		MBooking_New mybooking = new MBooking_New(getCtx(), p_Sh_Booking_ID,get_TrxName());
		mybooking.set_TrxName(get_TrxName());
/*		if (!mybooking.isSalesLead() && mybooking.getC_BPartner_ID() != 0)
			throw new AdempiereUserError("mybooking already converted");*/   
		
		MInvoice_New myinv = new MInvoice_New(getCtx(),0,get_TrxName());
		myinv.set_TrxName(get_TrxName());
/*		if ( !Util.isEmpty(mybooking.getBPName()) )
			myinv.setName(mybooking.getBPName());
		else
			myinv.setName(mybooking.getName());
			myinv.setDescription(mybooking.getDescription());  */  
			
/*	      System.out.println("organization " + mybooking.getAD_Org_ID());             
	      System.out.println("Client " + mybooking.getAD_Client_ID());
	      System.out.println("Customer " + mybooking.getB_Client_ID());  
	      System.out.println("booking id " + mybooking.getSh_Booking_ID());  */       

	      myinv.setC_BPartner_ID(mybooking.getB_Client_ID());      
	      myinv.setC_DocTypeTarget_ID(1000002);        			
			myinv.setM_PriceList_ID(1000000);       
			myinv.setIsSOTrx(true);      
			myinv.setAD_Org_ID(mybooking.getAD_Org_ID());
			myinv.setSh_Booking_ID(mybooking.getSh_Booking_ID());     
  
	/*	myinv.set_CustomColumn("C_Commodity_ID", mybooking.get_Value("C_Commodity_ID"));   
		myinv.set_CustomColumn("C_Country_ID", mybooking.get_Value("C_Country_ID"));  
		System.out.println("Commodity ID Here " + mybooking.get_Value("C_Commodity_ID"));     
		System.out.println("Country ID Here " + mybooking.get_Value("C_Country_ID"));        */  
		
	

		
	//	myinv.set_CustomColumn("C_Commodity_ID",mybooking.get_Value("C_Commodity_ID"));               


	     
		
		    
		
		if(myinv.save())
		{	

			System.out.println("Invoice Saved Done "+ myinv.getC_Invoice_ID());     
			 System.out.println("==========================================================================================================");			
		  String sql="Select c_oceanfreight,c_thc,c_caf,c_baf from Sh_Booking where IsActive='Y' and Sh_Booking_ID="+p_Sh_Booking_ID;       
		  pst = DB.prepareStatement(sql,get_TrxName());
		  rs=pst.executeQuery();   
		  
		  int myIntArray[]=new int[5];     
		    
		    while(rs.next())
		    {
		    	for(int x = 1; x<=4; x++)           
		    	{
		    	myIntArray[x]=rs.getInt(x);                
		    	}
		    }
		    System.out.println("=====================================================Charges Price=====================================================");             
		    for(int x = 1; x<=4; x++)           
	    	{
		    	 System.out.println(myIntArray[x]);                
	    	}
		    
		    System.out.println("==========================================================================================================");		   
		    String sql2="Select b_oceanfreight,b_thc,b_caf,b_baf from Sh_Booking where IsActive='Y' and Sh_Booking_ID="+p_Sh_Booking_ID;     
		    pst = DB.prepareStatement(sql2,get_TrxName());
		    rs2=pst.executeQuery();
		    String[] myStringArray = new String[5];            
		    while(rs2.next())     
		    {
		    	for(int x = 1; x<=4; x++)              
		    	{
		    		myStringArray[x]=rs2.getString(x);                         
		    	}
		    }
		    System.out.println("=====================================================Name Price=====================================================");                
		    for(int x = 1; x<=4; x++)           
	    	{
		    	 System.out.println(myStringArray[x]);                            
	    	}
		     
		  
        
	  for (int i=0; i < myIntArray.length; i++)                           
		  {
			
			  MInvoiceLine mabp = new MInvoiceLine(getCtx(),0,get_TrxName());
			  
			  System.out.println("INVOICE LINE Data " + rs.getString(1));                
			  int totalline = Integer.parseInt(rs.getString(1));     
			  
			  mabp.setC_Invoice_ID(myinv.getC_Invoice_ID());              
			  System.out.println("0");  
			  mabp.setAD_Org_ID(mybooking.getAD_Org_ID());
			  System.out.println("1"); 
			  mabp.setM_Product_ID(1000010);            
			  System.out.println("2"); 
			  mabp.setPriceEntered(BigDecimal.valueOf(myIntArray[i]));
			  System.out.println("3"); 
			  mabp.setPriceActual(BigDecimal.valueOf(myIntArray[i]));
			  System.out.println("4"); 
			  mabp.setLineNetAmt(BigDecimal.valueOf(myIntArray[i]));
			  System.out.println("5"); 
			  mabp.setLineTotalAmt(BigDecimal.valueOf(myIntArray[i]));
			  System.out.println("6"); 
			  mabp.setQtyInvoiced(BigDecimal.valueOf(1));
			  System.out.println("7"); 
			  mabp.setQtyEntered(BigDecimal.valueOf(1));
			  System.out.println("8"); 
			  mabp.setC_Tax_ID(1000000);
			  System.out.println("9"); 
			  mabp.setTaxAmt(BigDecimal.valueOf(myIntArray[i]));               
			  System.out.println("10");       
			  mabp.setLine(10);                    
			  System.out.println("11");			   
			  mabp.setPriceList(BigDecimal.valueOf(0));                                
			  System.out.println("12");    
			 
			  if(mabp.save())
			  {
			  System.out.println("Invoice Line Saved Done");
			  }
			  else
				  System.out.println("Invoice Line Not Saved");      
		  }    
    
		  
		  
		}
		addBufferLog(myinv.getC_Invoice_ID(), null, null, "@Sh_Booking_ID@ @Created@", MInvoice.Table_ID, myinv.getC_Invoice_ID());      
		//startttttttttttttt
		 
		

		

		

		
		return "@OK@";
	}

	@Override
	protected void prepare() {
		
		ProcessInfoParameter[] paras = getParameter();
		for (ProcessInfoParameter para : paras)
		{
			String name = para.getParameterName();
			if ( Util.isEmpty(name) )   
				;
			else if ("Sh_Booking_ID".equals(name))
				p_Sh_Booking_ID = para.getParameterAsInt();
			else 
			{
				log.log(Level.WARNING, "Unknown parameter: " + name);
			}
			
			if ( MBooking_New.Table_ID == getTable_ID() )
				p_Sh_Booking_ID  = getRecord_ID();
			   
			
		}

	}
   
}