package br.com.proway.vo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.RevertCommand;
import org.eclipse.jgit.api.RmCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteSession;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;

public class RepositorioGit implements Serializable {

    private String url;
    private String repositorio;
    private Map<String, File> branches;
    private List<File> conficts;
    private List<RevCommit> commitsLocais;

    {
        this.branches = new HashMap<>();
    }

    public RepositorioGit(String repositorio, String url) throws FileNotFoundException, IOException, ClassNotFoundException {
        setRepositorio(repositorio, url);
        commitsLocais = new ArrayList();
    }

    public RepositorioGit(String url) throws IOException {
        this.repositorio = "temp/repositorio_criacao_projeto";
        File f = new File(repositorio);
        if (Files.exists(f.toPath())) {
            this.deleteFolder(f);
        }
        Files.createDirectory(f.toPath());
        this.url = url;
    }

    public static boolean isValidCredentials(String url, String user, String pass) {
        boolean result = false;
        LsRemoteCommand lsrc = new LsRemoteCommand(null);
        lsrc.setRemote(url);
        lsrc.setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pass));
        try {
            result = lsrc.call() != null;
        } catch (Exception e) {
            //e.printStackTrace();
            result = false;
        }
        return result;
    }

    public static boolean isValidRemoteRepository(String repo) throws IOException, GitAPIException {
        boolean result = false;
        try {
            Git.lsRemoteRepository().setRemote(repo).setHeads(true).call();
            result = true;
        } catch (Exception e) {
            if (e.getMessage().endsWith("Authentication is required but no CredentialsProvider has been registered")) {
                result = true;
                return result;
            }
     //       System.out.println(e.getMessage());
//            System.out.println("Erro previsto");
   //         e.printStackTrace();
        }
        return result;
    }

    public static boolean isValidLocalRepository(String repo) {
        boolean result;
        try {
            URIish repoUri = new URIish(repo);
            result = new FileRepository(repoUri.getPath()).getObjectDatabase().exists();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public void setRepositorio(String repositorio, String url) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchFileException {
        if (!Files.exists(Paths.get(repositorio))) {
            Files.createDirectory(Paths.get(repositorio));
        }
        this.repositorio = repositorio;
        this.url = url;
        Stream<Path> files = Files.list(Paths.get(this.repositorio));
        files.forEach((p) -> {
            String[] aux = p.toString().split("[\\\\]");
            String fileName = aux[aux.length - 1];
            if (!Files.isDirectory(Paths.get(p.toUri()))) {
                return;
            }
            System.out.println(fileName);
            branches.put(fileName, p.toFile());
        });
        //this.loadCredenciais();
    }

    public String getRepositorio() {
        return this.repositorio;
    }

    /**
     * Sincroniza os arquivos de uma branch local com a remota. Se houver
     * confitos, ocorre um merge local e a sincronização é cancelada retornando
     * uma lista de strings com as localizações dos arquivos conflitantes. O
     * ideal é que essa seja a oportunidade de o usuário resolver esse conflitos
     * para então sincronizar com o repositório remoto sem conflito nenhum
     *
     * @param branch nome da branch que será sinzrinozada
     * @param files lista de arquivos que serão commitados
     * @param message mensagem do commit
     * @param name nome do autor do commit
     * @param email email do autor do commit
     * @return uma lista de string com a localizações dos conflitos, ou null se
     * não houver conflito nenhum
     * @throws IOException Caso não for encontrada a branch localmente
     * @throws GitAPIException Caso orocorrer algum erro na interação com a api
     * do Git
     */
//    public List<File> sinc(String branch, List<File> files,
//            List<File> toDelete, String message, String name, String email) throws IOException, GitAPIException {
//        conficts = null;
//        this.createCommit(branch, files, toDelete, message, name, email);
//        this.pull(branch, user, pass);
//        if (conficts != null) {
//            return conficts;
//        }
//        this.push(branch);
//        return null;
//    }
    public List<File> sinc(String branch,
            List<File> files,
            List<File> toDelete,
            String message,
            String name,
            String email,
            String username,
            String password) throws IOException, GitAPIException {
        conficts = null;
        this.createCommit(branch, files, toDelete, message, name, email);
        this.pull(branch, username, password);
        if (conficts != null) {
            return conficts;
        }
        this.push(branch, username, password);
        return null;
    }

    public List<File> sinc(String branch,
            String message,
            String name,
            String email,
            String username,
            String password) throws IOException, GitAPIException {
        conficts = null;
        this.createCommit(branch, message, name, email);
        this.pull(branch, username, password);
        if (conficts != null) {
            return conficts;
        }
        this.push(branch, username, password);
        return null;
    }

    /**
     *
     * @param branch
     * @return modified na posição 0, newFiles na posição 1, conflicts na
     * posição 2, deleted na posição 3
     * @throws IOException
     * @throws GitAPIException
     */
    public ArrayList<File>[] changes(String branch) throws IOException, GitAPIException {
        ArrayList<File> modified = new ArrayList();
        ArrayList<File> newFiles = new ArrayList();
        ArrayList<File> conflicts = new ArrayList();
        ArrayList<File> deleted = new ArrayList();
        Status status = Git.open(this.branches.get(branch)).status().call();
        for (String str : status.getModified()) {
            modified.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
        }
        for (String str : status.getUntracked()) {
            newFiles.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
        }
        for (String str : status.getConflicting()) {
            conflicts.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
        }
        for (String str : status.getMissing()) {
            deleted.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
        }
        return new ArrayList[]{modified, newFiles, conflicts, deleted};
    }

    public ArrayList<File> uncommitedChangeFiles(String branch) throws IOException, GitAPIException {
        ArrayList<File> modified = new ArrayList();
        for (String str : Git.open(this.branches.get(branch)).status().call().getModified()) {
            modified.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
        }
        return modified;
    }

    public ArrayList<File> newFiles(String branch) throws IOException, GitAPIException {
        ArrayList<File> newFiles = new ArrayList();
        for (String str : Git.open(this.branches.get(branch)).status().call().getUntracked()) {
            newFiles.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
        }
        return newFiles;
    }

    /**
     * @param branch
     * @throws IOException
     * @throws GitAPIException
     * @throws TransportException quando as credenciais forem inválidas ou o
     * usuário não for autorizado a fazer um push nesse repositorio!
     */
//    public void push(String branch) throws IOException, GitAPIException, TransportException {
//        PushCommand pushCommand = Git.open(branches.get(branch)).push();
//        pushCommand
//                .setPushAll()
//                .setRemote(url)
//                .call();
//    }
    public void push(String branch, String username, String password) throws IOException, GitAPIException, TransportException {
        PushCommand pushCommand = Git.open(branches.get(branch)).push();
        pushCommand
                .setPushAll()
                .setRemote(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .call();
    }

    public void createCommit(String branch, List<File> files, List<File> toDelete,
            String message, String author, String email) throws IOException, GitAPIException {

        Git git = Git.open(branches.get(branch));
        String f;

        if (!files.isEmpty()) {
            AddCommand addCommand = git.add();
            for (File file : files) {
                f = file.toString().replace(this.repositorio + File.separatorChar + branch, "");
                f = f.replaceAll("\\\\", "/");
                f = f.substring(1, f.length());
                addCommand.addFilepattern(f);
            }
            addCommand.call();
        }

        if (!toDelete.isEmpty()) {
            RmCommand rmCommand = git.rm();
            for (File file : toDelete) {
                f = file.toString().replace(this.repositorio + File.separatorChar + branch, "");
                f = f.replaceAll("\\\\", "/");
                f = f.substring(1, f.length());
                rmCommand.addFilepattern(f);
            }
            rmCommand.call();
        }

        CommitCommand commitCommand = Git.open(branches.get(branch)).commit();
        commitsLocais.add(
                commitCommand
                        .setMessage(message)
                        .setAuthor(author, email)
                        .call()
        );

    }

    public void createCommit(String branch, String message, String author, String email) throws IOException, GitAPIException {
        Git git = Git.open(branches.get(branch));
        AddCommand addCommand = git.add();
        addCommand.addFilepattern(".");
        addCommand.call();
    }

    /**
     * @param branch nome da branch que será clonada
     * @throws DirectoryNotEmptyException se o repositório já existe e não está
     * vazio
     */
    public void cloneBranch(String branch, String user, String pass) throws DirectoryNotEmptyException, IOException, GitAPIException {
        createBranchFolder(branch);
        Git.cloneRepository()
                .setURI(url)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pass))
                .setDirectory(branches.get(branch))
                .setBranch(branch)
                .call();
    }

    private void createBranchFolder(String branch) throws IOException {
        branches.put(branch, new File(repositorio + "/" + branch));
        Path p = Paths.get(repositorio + "/" + branch);
        if (Files.exists(p)) {
            deleteFolder(p.toFile());
        }
        Files.createDirectory(p);
    }

//    public void pull(String branch) throws IOException, GitAPIException {
//        PullResult pr = Git.open(branches.get(branch)).pull().call();
//        Map<String, int[][]> conflicts = pr.getMergeResult().getConflicts();
//        if (conflicts != null) {
//            this.conficts = new ArrayList<File>();
//            conflicts.forEach((str, arr) -> {
//                //Nome do arquivo
//                conficts.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
//            });
//        }
//    }
    public void pull(String branch, String user, String pass) throws IOException, GitAPIException {
        PullResult pr = Git.open(branches.get(branch))
                .pull()
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pass))
                .call();
        Map<String, int[][]> conflicts = pr.getMergeResult().getConflicts();
        if (conflicts != null) {
            this.conficts = new ArrayList<File>();
            conflicts.forEach((str, arr) -> {
                //Nome do arquivo
                conficts.add(new File(this.repositorio + File.separatorChar + branch + File.separatorChar + str));
            });
        }
    }

    public void createBranch(String name, String startPoint, String user, String pass) throws Exception {
        createBranchFolder(name);
        startPoint = startPoint == null || startPoint.isEmpty() ? "master" : startPoint; //.split("/")[startPoint.split("/").length - 1];
        Git git = Git.cloneRepository()
                .setURI(url)
                .setBranch(startPoint)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pass))
                .setDirectory(branches.get(name))
                .call();
        git.branchCreate().setName(name).call();
        git.checkout().setName(name).call();//switchToBranch(git, name);
        this.push(name, user, pass);
    }

    public void merge(String branchToUpdate, String branchHead) throws IOException, GitAPIException {
        ObjectId oi = Git.open(branches.get(branchHead)).getRepository().resolve(branchHead);
        Git git = Git.open(branches.get(branchToUpdate));

        CheckoutCommand coCmd = git.checkout();
        // Commands are part of the api module, which include git-like calls
        coCmd.setName(branchToUpdate);
        coCmd.setCreateBranch(false); // probably not needed, just to make sure
        coCmd.call(); // switch to "master" branch

        MergeCommand mgCmd = git.merge();
        mgCmd.include(oi); // "foo" is considered as a Ref to a branch
        MergeResult res = mgCmd.call(); // actually do the merge

        if (res.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
            System.out.println(res.getConflicts().toString());
            // inform the user he has to handle the conflicts
        }
        /*
        Git.open(branches.get(branchHead))
                .add()
                .addFilepattern(".")
                .call();
        
        
        ObjectId oi = Git
                .open(branches.get(branchHead)).commit()
                .setMessage(message)
                .setAuthor(author, email)
                .call()
                .getId();
        
        Git.open(branches.get(branchToUpdate)).checkout().setName(branchToUpdate).call();
        Git.open(branches.get(branchToUpdate)).merge().include(oi);*/
    }

    public List<String> getBranches(String user, String pass) throws IOException, GitAPIException {
        Collection<Ref> refs = Git.lsRemoteRepository().setRemote(this.url).setCredentialsProvider(new UsernamePasswordCredentialsProvider(user, pass)).setHeads(true).call();
        List<String> branches = new ArrayList();
        for (Ref ref : refs) {
            branches.add(ref.getName().substring(11, ref.getName().length()));
        }
        return branches;
        //return Git.lsRemoteRepository().setHeads(true).setRemote(this.url).callAsMap().keySet();
    }

    public void deleteBranch(String branch) throws IOException, GitAPIException {
        Git git = Git.open(this.branches.get("master"));
        git.checkout().setName("master").call();
        git.branchDelete().setBranchNames("refs/heads/" + branch).call();
        RefSpec refSpec = new RefSpec()
                .setSource(null)
                .setForceUpdate(true)
                .setDestination("refs/heads/" + branch);
        git
                .push()
                .setRefSpecs(refSpec)
                .setRemote(url)
                .call();
        deleteFolder(branches.get(branch));
    }

    public void deleteBranch(String branch, String username, String password) throws IOException, GitAPIException {
        Git git = Git.open(this.branches.get("master"));
        git.checkout().setName("master").call();
        git.branchDelete().setBranchNames("refs/heads/" + branch).call();
        RefSpec refSpec = new RefSpec()
                .setSource(null)
                .setForceUpdate(true)
                .setDestination("refs/heads/" + branch);
        git
                .push()
                .setRefSpecs(refSpec)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setRemote(url)
                .call();
        deleteFolder(branches.get(branch));
    }

    public List<File> listBranch(String branch) {
        ArrayList<File> files = new ArrayList<>();
        ArrayList<File> list = new ArrayList();
        list.addAll(Arrays.asList(branches.get(branch).listFiles()));
        list.remove(new File(branches.get(branch) + "/.git"));
        for (File file : list) {
            if (file.isDirectory()) {
                list(files, file);
            } else {
                files.add(file);
            }
        }
        return files;
    }

    private void list(ArrayList<File> arr, File directory) {
        List<File> list = Arrays.asList(directory.listFiles());
        for (File file : list) {
            if (file.isDirectory()) {
                list(arr, file);
            } else {
                arr.add(file);
            }
        }
    }

    public void deleteFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteFolder(child);
            }
        }
        fileOrDirectory.delete();
    }

    @Override
    public String toString() {
        return branches.keySet().toString();
    }

}
