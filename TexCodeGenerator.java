
public class TexCodeGenerator 
{
	public static void main(String args[])
	{
		for(int i = 0;i<1000;i++)
		{
			System.out.println("\\begin{frame}");
			System.out.println("\\frametitle{Image"+(i+1)+"}");
			System.out.println("\\includegraphics[scale=0.42]{./screenshots/karate/karateAvgNodeJaccard/ss_"+i+".png}");
			System.out.println("\\end{frame}");
		}
	}

}
