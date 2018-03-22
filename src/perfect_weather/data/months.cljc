(ns perfect-weather.data.months)

(def months 
  ["January" 
   "February" 
   "March" 
   "April" 
   "May" 
   "June" 
   "July" 
   "August" 
   "September" 
   "October" 
   "November" 
   "December"])

(def months-abbr 
  (mapv #(apply str (take 3 %)) months))
